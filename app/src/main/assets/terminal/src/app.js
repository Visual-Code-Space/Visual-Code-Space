import { Terminal } from "@xterm/xterm";
import '@xterm/xterm/css/xterm.css';

const term = new Terminal({ cursorBlink: true });
term.open(document.getElementById('terminal'));

// Setup WebSocket connection to the backend server
const socket = new WebSocket('ws://localhost:8080');

// Send commands to the server when the user presses 'Enter'
term.onKey(({ key, domEvent }) => {
    if (domEvent.key === 'Enter') {
        const command = term.buffer.active.getLine(0).translateToString();
        socket.send(command);  // Send the command to the backend
        term.write('\r\n');  // New line after pressing Enter
    } else {
        term.write(key);  // Display the key typed in the terminal
    }
});

// Listen for incoming messages from the server
socket.onmessage = (event) => {
    term.write(event.data);  // Write server response to the terminal
};
