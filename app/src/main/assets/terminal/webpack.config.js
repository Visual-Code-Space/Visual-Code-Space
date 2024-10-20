const path = require('path');

module.exports = {
  entry: './src/app.js',
  output: {
    filename: 'main.js',
    path: path.resolve(__dirname, 'dist'),
  },
  module: {
    rules: [
      {
        test: /\.css$/,  // This will handle CSS imports
        use: ['style-loader', 'css-loader'],
      },
    ],
  },
  mode: 'production',  // Use 'development' for easier debugging
};
