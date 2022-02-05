import axios from 'axios';

const Util = {
  //linux and windows
  // apiPath: 'http://127.0.0.1:8082'
  apiPath: 'http://192.168.1.10:8082'
  // apiPath: 'http://120.*.*.30:8082'
};

//Ajax通用配置
Util.ajax = axios.create({
  baseURL: Util.apiPath,
  //请求将携带Cookie
  withCredentials: true
});

//添加响应拦截器
/*Util.ajax.interceptors.response.use((res) => {
  return res.data;  //只返回res的data部分，其他部分不要了
},(err) => {
  console.log("error?");
  return err;
});*/

export default Util;  //暴露的是Util
