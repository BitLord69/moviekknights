module.exports = {
  devServer: {
    //https: true,
    proxy: {
      '^/rest': {
        target: 'http://localhost:5050/',
        changeOrigin: true,
      },
      '^/rest/movies': {
        target: 'http://localhost:5050/',
        changeOrigin: true,
      },
      '^/api': {
        target: 'http://localhost:5050/',
        changeOrigin: true,
        // pathRewrite: { "^/api/": "/api/" },
        logLevel: "debug",
      },
      '^/api/auth/storeauthcode': {
        target: 'http://localhost:5050/',
        changeOrigin: true,
        // pathRewrite: { "^/api/": "/api/" },
        logLevel: "debug",
      },
    }
  }
}