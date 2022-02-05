const http = require('http'); /* http模块为内置模块，直接使用即可 */
const request = require('request');

const hostName = '127.0.0.1';
const port = 8010;
const portPost = 8011;


//创建一个API代理服务
const apiServer = http.createServer((req, res) => {
  const url = 'http://localhost:8080' + req.url;

  const options = {
    url: url
  };

  function callback(error, response, body) {
    if(!error && response.statusCode === 200){
      //设置编码类型，否则中文显示乱码
      //res.setHeader('Content-Type', 'text/plain;charset=UTF-8');
      res.setHeader('Content-Type', 'application/json;charset=utf-8');
      //设置所有域允许跨域
      res.setHeader('Access-Control-Allow-Origin', '*');
      //返回代理后的内容
      res.setHeader('Access-Control-Allow-Credentials','true');

      res.setHeader('Access-Control-Allow-Headers','*');

      res.end(body);
    }
  }
  request.get(options, callback);
});

//监听8010端口  先定义const apiService，后调用apiServer.listen才有效
apiServer.listen(port, hostName, () => {
  console.log(`接口代理运行在http://${hostName}:${port}/`);  //使用反引号
});


//创建一个API代理服务，用来代理8011端口的POST请求
const postApiServer = http.createServer((req, res) => {
  const url = 'http://localhost:8080' + req.url;

  const options = {
    url: url,
    /*body: JSON.stringify("1123"),   这里来传递post的请求参数  */
    body: req
  };

  function callback(error, response, body) {
    if(!error && response.statusCode === 200){

      //console.log("for response....................");
      //console.log(response.headers["set-cookie"]);

      //设置编码类型，否则中文显示乱码
      //res.setHeader('Content-Type', 'text/plain;charset=UTF-8');
      res.setHeader('Content-Type', 'application/json;charset=utf-8');
      //设置所有域允许跨域
      //res.setHeader('Access-Control-Allow-Origin', '*');
      res.setHeader('Access-Control-Allow-Origin', '*');   /* req.headers.origin */
      //返回代理后的内容
      res.setHeader('Access-Control-Allow-Credentials','true');

      res.setHeader('Access-Control-Allow-Headers','*');

      res.setHeader('Set-Cookie', 'l=a123456;Path=/;Domain=www.zhangkaikuayu.com');

/*      if(typeof(response.headers["set-cookie"]) !== "undefined"){     //undefined的正确判断方法
        res.setHeader('set-cookie', response.headers["set-cookie"]);
      }*/


      console.log("for res................................................................");
      console.log(res.getHeaders());

      res.end(body);
    }
  }
  request.post(options,callback);   /* 这里，设定代理服务是post请求！！！ */
});

//监听8011端口
postApiServer.listen(portPost, hostName, () => {
  console.log(`接口代理运行在http://${hostName}:${portPost}/`);
});



