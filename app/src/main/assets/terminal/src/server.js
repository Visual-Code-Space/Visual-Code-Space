const express = require('express');
const WebSocket = require('ws');
const { spawn } = require('child_process');

// Create an Express app
const app = express();
const port = 8080;

// Serve static files (including your HTML and JS)
app.use(express.static(__dirname));

// Start an HTTP server
const server = app.listen(port, () => {
    console.log(`Server is running on http://localhost:${port}`);
});

// Set up a WebSocket server
const wss = new WebSocket.Server({ server });

wss.on('connection', (ws) => {
    console.log('Client connected.');

    // Listen for commands from the client
    ws.on('message', (message) => {
        const command = message.toString().trim(); // Convert Buffer to string and trim whitespace
        console.log(`Received command: ${command}`);

        // Execute the command in a shell
        const shell = spawn(command, [], { shell: true });

        // Send output back to the client in real time
        shell.stdout.on('data', (data) => {
            ws.send(data.toString());
        });

        shell.stderr.on('data', (data) => {
            ws.send(data.toString());
        });

        shell.on('close', (code) => {
            ws.send(`\nCommand exited with code ${code}\n`);
        });
    });

    // Notify when the client disconnects
    ws.on('close', () => {
        console.log('Client disconnected.');
    });
});
