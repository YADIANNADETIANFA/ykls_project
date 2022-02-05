import Vue from 'vue';
import VueRouter from 'vue-router';
import Routers from './router/index';
import Vuex from 'vuex';
import App from './myApp.vue';
import VueCookies from 'vue-cookies';
import ElementUI from 'element-ui';
import 'element-ui/lib/theme-chalk/index.css';
import mavonEditor from 'mavon-editor';
import 'mavon-editor/dist/css/index.css'

import './style.css';
import './iconfont/iconfont.css'

Vue.use(VueRouter);
Vue.use(Vuex);
Vue.use(VueCookies);
Vue.use(ElementUI);
Vue.use(mavonEditor);

//路由配置
const RouterConfig = {
  //使用HTML5的History路由模式
  model: 'history',
  routes: Routers
}

const router = new VueRouter(RouterConfig);

router.beforeEach((to,from,next) => {
  window.document.title = to.meta.title;
  next();
});

router.afterEach(() => {  //我们未使用任何参数
  window.scrollTo(0,0);
});

const store = new Vuex.Store({
  state:{
    //是否展示提问弹窗
    questionWindow: false,
    //是否展示发送消息弹窗
    messageWindow: false,
    //前台存放的User信息数据
    user: ''
  },
  mutations: {
    SET_USER(state, userVal) {
      state.user = userVal;
    },
    REMOVE_USER(state) {
      state.user = '';
    },
    setQuestionWindow(state, isShowQuestionWindow){
      state.questionWindow = isShowQuestionWindow;
    },
    setMessageWindow(state, isShowMessageWindow){
      state.messageWindow = isShowMessageWindow;
    },
    SET_TOKEN:(state, token) => {
      localStorage.setItem("token", token);
    },
    REMOVE_TOKEN:(state) => {
      localStorage.setItem("token", '');
    }
  }
});


new Vue({
  el: '#app',
  router:router,
  store: store,
  render: h => {
    return h(App)
  }
});
