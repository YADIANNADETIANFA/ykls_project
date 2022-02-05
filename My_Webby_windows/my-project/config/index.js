'use strict'
// Template version: 1.3.1
// see http://vuejs-templates.github.io/webpack for documentation.

const path = require('path')

module.exports = {
  dev: {

    // Paths
    assetsSubDirectory: 'static',
    assetsPublicPath: '/',
    proxyTable: {
      //开发环境跨域，上线的话需要nodejs反向代理
/*      '/loginWebby':{
        target: 'http://127.0.0.1:8082',
        changeOrigin: true
      },*/
/*      '/indexWebby':{
        target: 'http://127.0.0.1:8080',
        changeOrigin: true
      },*/
/*      '/logoutWebby':{
        target: 'http://127.0.0.1:8080',
        changeOrigin: true
      },*/
/*      '/questionWebby/add':{
        target: 'http://127.0.0.1:8080',
        changeOrigin: true
      },*/
/*      '/msgWebby/addMessage':{
        target: 'http://127.0.0.1:8080',
        changeOrigin: true
      },*/
      //记得模糊匹配用 *
/*      '/questionWebby/!*':{    //limited及以上
        target: 'http://127.0.0.1:8080',
        changeOrigin: true
      },*/
/*      '/userWebby/!*':{    //limited及以上
        target: 'http://127.0.0.1:8080',
        changeOrigin: true
      },*/
/*      '/msgWebby/list':{
        target: 'http://127.0.0.1:8080',
        changeOrigin: true
      },*/
/*      '/msgWebby/detail':{
        target: 'http://127.0.0.1:8080',
        changeOrigin: true
      },*/
/*      '/addCommentWebby':{
        target: 'http://127.0.0.1:8080',
        changeOrigin: truelikeWebby
      },*/
/*      '/likeWebby':{
        target: 'http://127.0.0.1:8080',
        changeOrigin: true
      },
      '/dislikeWebby':{
        target: 'http://127.0.0.1:8080',
        changeOrigin: true
      },*/
/*      '/userWebby/!*!/followers':{
        target: 'http://127.0.0.1:8080',
        changeOrigin: true
      },
      '/userWebby/!*!/followees':{
        target: 'http://127.0.0.1:8080',
        changeOrigin: true
      },*/
/*      '/followUserWebby':{
        target: 'http://127.0.0.1:8080',
        changeOrigin: true
      },
      '/unfollowUserWebby':{
        target: 'http://127.0.0.1:8080',
        changeOrigin: true
      },
      '/followQuestionWebby':{
        target: 'http://127.0.0.1:8080',
        changeOrigin: true
      },
      '/unfollowQuestionWebby':{
        target: 'http://127.0.0.1:8080',
        changeOrigin: true
      },*/
/*      '/pullfeedsWebby':{
        target: 'http://127.0.0.1:8080',
        changeOrigin: true
      },
      '/pushfeedsWebby':{
        target: 'http://127.0.0.1:8080',
        changeOrigin: true
      }*/
      //重定向的时候，根本就没走proxy，设置了没任何用处
/*      '/loginWebby?next=/indexWebby':{
        target: 'http://127.0.0.1:8080',
        changeOrigin: true
      },*/


      //修改这里的跨域，记得重启服务！
    },

    // Various Dev Server settings
    host: 'localhost', // can be overwritten by process.env.HOST
    port: 8081, // can be overwritten by process.env.PORT, if port is in use, a free one will be determined
    autoOpenBrowser: false,
    errorOverlay: true,
    notifyOnErrors: true,
    poll: false, // https://webpack.js.org/configuration/dev-server/#devserver-watchoptions-


    /**
     * Source Maps
     */

    // https://webpack.js.org/configuration/devtool/#development
    //devtool: 'cheap-module-eval-source-map',
    devtool: 'source-map',

    // If you have problems debugging vue-files in devtools,
    // set this to false - it *may* help
    // https://vue-loader.vuejs.org/en/options.html#cachebusting
    cacheBusting: true,

    cssSourceMap: true
  },

  build: {
    // Template for index.html
    index: path.resolve(__dirname, '../dist/index.html'),

    // Paths
    assetsRoot: path.resolve(__dirname, '../dist'),
    assetsSubDirectory: 'static',
    assetsPublicPath: '/',

    /**
     * Source Maps
     */

    productionSourceMap: true,
    // https://webpack.js.org/configuration/devtool/#production
    devtool: '#source-map',

    // Gzip off by default as many popular static hosts such as
    // Surge or Netlify already gzip all static assets for you.
    // Before setting to `true`, make sure to:
    // npm install --save-dev compression-webpack-plugin
    productionGzip: false,
    productionGzipExtensions: ['js', 'css'],

    // Run the build command with an extra argument to
    // View the bundle analyzer report after build finishes:
    // `npm run build --report`
    // Set to `true` or `false` to always turn it on or off
    bundleAnalyzerReport: process.env.npm_config_report
  }
}
