const routers = [
  {
    path: '/loginWebby',
    meta:{
      title: '伊卡洛斯_登陆系统',
      showNav: false
    },
    component: (resolve) => require(['../views/loginWebby.vue'],resolve)
  },
  {
    path: '/indexWebby',
    meta:{
      title: '伊卡洛斯_论坛大厅',
      showNav: true
    },
    component: (resolve) => require(['../views/indexWebby.vue'],resolve)
  },
  {
    path: '/questionWebby/:id',
    meta:{
      title: '伊卡洛斯_问题详情',
      showNav: true
    },
    component: (resolve) => require(['../views/questionWebby.vue'],resolve)
  },
  {
    path: '/userWebby/:id',
    meta:{
      title: '伊卡洛斯_我的主页',
      showNav: true
    },
    component: (resolve) => require(['../views/userWebby.vue'],resolve)
  },
  {
    path: '/userWebby/:id/followers',
    meta:{
      title: '伊卡洛斯_我的粉丝',
      showNav: true
    },
    component: (resolve) => require(['../views/followersWebby.vue'],resolve)
  },
  {
    path: '/userWebby/:id/followees',
    meta:{
      title: '伊卡洛斯_我所关注',
      showNav: true
    },
    component: (resolve) => require(['../views/followeesWebby.vue'],resolve)
  },
  {
    path: '/pushfeeds',
    meta:{
      title: '伊卡洛斯_动态push',
      showNav: true
    },
    component: (resolve) => require(['../views/pushWebby.vue'],resolve)
  },
  {
    path: '/pullfeeds',
    meta:{
      title: '伊卡洛斯_动态pull',
      showNav: true
    },
    component: (resolve) => require(['../views/pullWebby.vue'],resolve)
  },
  {
    path: '/msgWebby/list',
    meta:{
      title: '伊卡洛斯_私信',
      showNav: true
    },
    component: (resolve) => require(['../views/msgListWebby.vue'],resolve)
  },
  {
    path: '/msgWebby/detail',
    meta:{
      title: '伊卡洛斯_私信',
      showNav: true
    },
    component: (resolve) => require(['../views/msgDetailWebby.vue'],resolve)
  },
  {
    path: '/indexUI',
    meta:{
      title: 'Container',
      showNav: true
    },
    component: (resolve) => require(['../views/indexUI.vue'],resolve)
  },
  {
    path: '/blogEditorWebby',
    meta:{
      title: '伊卡洛斯_博客编辑',
      showNav: true
    },
    component: (resolve) => require(['../views/blogEditorWebby.vue'],resolve)
  },
  {
    path: '/searchWebby',
    meta:{
      title: '伊卡洛斯_搜索',
      showNav: true
    },
    component: (resolve) => require(['../views/searchWebby.vue'],resolve)
  }
];
export default routers;

//vue 解决重复点击路由报错问题
//https://www.cnblogs.com/jervy/p/13354877.html
import Vue from 'vue';
import VueRouter from 'vue-router';
Vue.use(VueRouter)

const originalPush = VueRouter.prototype.push
VueRouter.prototype.push = function push(location) {
  return originalPush.call(this, location).catch(err => err)
}

