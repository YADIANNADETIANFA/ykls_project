import axios from 'axios';

axios.defaults.withCredentials = true;

const UtilPost = {
  //linux and windows
  //曾经误导我们对proxy的跨域设置，所以我们就都注释掉了

  // apiPath: 'http://127.0.0.1:8082'
  apiPath: 'http://192.168.1.10:8082'
  // apiPath: 'http://120.*.*.30:8082'
};

//Ajax通用配置
UtilPost.ajax = axios.create({
  //同上
  baseURL: UtilPost.apiPath,

  //写了，但是开发环境下跨域，没起什么作用。。。
  withCredentials: true
});

//别拦了，有用的信息，就都放过去
//添加响应拦截器
/*UtilPost.ajax.interceptors.response.use((res) => {
  return res.data;
},(err) => {
  console.log("error?");
  return err;
});*/

export default UtilPost;
