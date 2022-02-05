<template>
  <div id="dropDownList" v-clickoutside="handleClose" v-cloak>
    <div id="divButton" @click="show = !show">
      <img id="myImg" :src=this.$store.state.userHeadImg :alt="'痞老板未显示出来'">
        <span id="spanUserName">{{ this.userName }}</span>
    </div>
    <div class="dropDown" v-show="show">
      <router-link class="dropDownList" :to="'/userWebby/' + this.userId">
        <span class="iconfont icon-zhuye listIconFont"></span>我的主页
      </router-link>
      <router-link class="dropDownList" :to="'/userWebby/' + this.userId + '/followers'">
        <span class="iconfont icon-zhuanshufensi listIconFont"></span>我的粉丝
      </router-link>
      <router-link class="dropDownList" :to="'/userWebby/' + this.userId + '/followees'">
        <span class="iconfont icon-guanzhu listIconFont"></span>我所关注
      </router-link>
      <router-link class="dropDownList" to="/pushfeeds">
        <span class="iconfont icon-dongtai listIconFont"></span>动态push
      </router-link>
      <router-link class="dropDownList" to="/pullfeeds">
        <span class="iconfont icon-dongtai1 listIconFont"></span>动态pull
      </router-link>
      <router-link class="dropDownList" to="/msgWebby/list">
        <span class="iconfont icon-youjian listIconFont"></span>私信
      </router-link>
      <button class="dropDownListButton" @click="logout">
        <span class="iconfont icon-tuichu listIconFont"></span>
        <span style="font-size: 15px">退出</span>
      </button>
    </div>
  </div>
</template>

<script>
import $ from '../libs/util';

export default {
  name: "dropDownList",
  data() {
    return{
      show: false
    }
  },
  props:{

  },
  methods:{
    handleClose: function(){
      this.show = false;
    },
    logout(){

      const _this = this;

      $.ajax.get(
        'logoutWebby',
        {
          headers: {
            "Authorization": localStorage.getItem("token")
          }
        }).then((res) => {
        if(res.data.code === "my200"){
          _this.$store.commit("REMOVE_TOKEN");
          _this.$store.commit("REMOVE_USER");
          _this.$router.push({path: '/loginWebby'});
        }
      }).catch(error => {
        console.log(error);
        alert("退出登录异常，请联系管理员");
      })
    }
  },
  computed:{
    userId(){
      return this.$store.state.user.id;
    },
    userName(){
      return this.$store.state.user.name;
    }
  },
  directives:{
    //局部注册自定义指令
    'clickoutside':{
      bind: function(el,binding,vnode){
        function documentHandler(e){
          if(el.contains(e.target)){
            return false;
          }
          if(binding.expression){
            binding.value(e);
          }
        }
        document.addEventListener('click',documentHandler);
      }
    }
  }
}
</script>

<style scoped>
[v-cloak]{
  display: none;
}
div#divButton{
  position: fixed;
  width: 180px;
  height: 44px;
}
img#myImg{
  width: 30px;
  height: 30px;
  float: left;
  margin-left: 10px;
  margin-top: 7px;
}
span#spanUserName{
  /*相对本应有的位置，进行移动*/
  position: relative;
  color: powderblue;
  top: 11px;
  left: 5px;
}
.dropDown{
  position: relative;
  top: 45px;
}
.dropDownList{
  display: block;
  color: white;
  background-color: rgba(0,0,0,0.7);
  border-color: darkgray;
  border-width: 1px;
  border-style: solid;  /*不设置样式实线，是不会显示出来的*/
  height: 40px;
  padding-left: 22px;
  padding-top: 5px;
  text-decoration: none;  /*去掉超链接下的下划线*/
}
.dropDownListButton{
  display: block;
  color: white;
  background-color: rgba(0,0,0,0.7);
  border-color: darkgray;
  border-width: 1px;
  border-style: solid;  /*不设置样式实线，是不会显示出来的*/
  height: 47px;
  padding-right: 70px;
  width: 180px;
}
.listIconFont{
  position: relative;
  top: 3px;
  font-size: 30px;
  padding-right: 10px;
  text-decoration: none;
}
</style>
