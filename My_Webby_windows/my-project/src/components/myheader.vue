<template>
  <div>
    <div id="upTools">
      <router-link v-if="isLogin === 0" id="toLogin" to="/loginWebby">登陆&注册</router-link>
      <button v-if="isLogin === 1" id="askQuestion" class="askQuestion" @click="handleQuestion">提问</button>
      <button v-if="isLogin === 1" id="sendMessage" class="sendMessage" @click="handleMessage">消息</button>
      <DropDownList id="dropDownList" v-if="isLogin === 1"></DropDownList>
      <router-link id="toPullFeeds" to="/pullfeeds">发现</router-link>
      <router-link id="backToIndex" to="/indexWebby">主站</router-link>
    </div>
  </div>
</template>

<script>
import DropDownList from './dropDownList';
import bus from '../libs/bus';

export default {
  name: "myheader",
  data(){
    return{
      isShowQuestionWindow: false,
      isShowMessageWindow: false
    }
  },
  components: {
    DropDownList
  },
  props:{

  },
  computed:{
    isLogin(){
      if(this.$store.state.user === ''){
        //未登录
        return 0;
      }else{
        //已登录
        return 1;
      }
    }
  },
  methods:{
    handleQuestion(){
      this.isShowQuestionWindow = !this.isShowQuestionWindow;
      this.$store.commit('setQuestionWindow', this.isShowQuestionWindow);
    },
    handleMessage(){
      this.isShowMessageWindow = !this.isShowMessageWindow;
      this.$store.commit('setMessageWindow', this.isShowMessageWindow);
    }
  },
  mounted() {
    bus.$on("closeQuestion", this.handleQuestion);
    bus.$on("closeMessage", this.handleMessage);
  }
}
</script>

<style scoped>
div#upTools{
  /*position: fixed;*/  /* 如果打开，indexWebby可使用，但userWebby与questionWebby不可使用 */
  background-color: rgba(0,0,0,0.7);
  margin-right: 32px;
  height: 48px;
  width: calc(100%);
}
#backToIndex{
  color: white;
  float: left;
  margin-left: 20px;
  margin-right: 20px;
  margin-top: 10px;
  font-size: 20px;
  font-style: italic;
  text-decoration: none;
}
#toPullFeeds{
  color: white;
  float: left;
  margin-left: 80px;
  margin-right: 20px;
  margin-top: 10px;
  font-size: 20px;
  font-style: italic;
  text-decoration: none;
}
#toLogin{
  color: white;
  float: right;
  margin-left: 60px;
  margin-right: 60px;
  margin-top: 10px;
  font-size: 20px;
  font-style: italic;
}
#dropDownList{
  /*位置固定，如果不固定，会在黑条里面，随着浏览器页面缩放，跟着一起左右乱跑*/
  position: fixed;
  width: 180px;
  height: 44px;
  left: 2000px;
  border-color: darkgray;
  border-width: 2px;
  border-style: solid;
}
.askQuestion{
   position: fixed;
   width: 80px;
   height: 40px;
   left: 1850px;
   top: 13px;
   border-color: darkgray;
   border-width: 2px;
   border-style: solid;
   border-radius: 6px;
   background: #666666;
   color: #fff;
   font-size: 14px;
 }
.sendMessage{
  position: fixed;
  width: 80px;
  height: 40px;
  left: 1750px;
  top: 13px;
  border-color: darkgray;
  border-width: 2px;
  border-style: solid;
  border-radius: 6px;
  background: #444444;
  color: #fff;
  font-size: 14px;
}

</style>

<!--  p212，router-link会被渲染为一个a标签，通过点击来进行跳转  -->
<!--  to不带参数，就不用v-bind动态绑定  -->
<!--  alt是当图片未能成功加载到时，显示出来的备注文本    -->
