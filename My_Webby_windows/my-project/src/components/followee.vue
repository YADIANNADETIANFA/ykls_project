<template>
  <div>
    <router-link :to="'/userWebby/' + this.info.user.id" style="text-decoration: none">
      <img id="UserImg" :src=this.info.user.headUrl :alt="'痞老板未展示出来'">
    </router-link>
    <div style="display: inline-block; position: relative; top: -5%;">
      <p style="font-weight: bold; color: #42b983">{{ this.info.user.name }}</p>
      <span style="color: cornflowerblue">{{ this.info.followerCount }}</span>
      <span>粉丝/</span>
      <span style="color: cornflowerblue">{{ this.info.followeeCount }}</span>
      <span>关注/</span>
      <span style="color: cornflowerblue">{{ this.info.commentCount }}</span>
      <span>回答/</span>
      <span style="color: cornflowerblue">548（待修改）</span>
      <span>赞同</span>
    </div>
    <div style="display: inline-block; vertical-align: top; position: relative; top: 35%; left: 60%">
      <el-button type="info" @click="this.unFollowTheUser" :style="this.unFollowUserButton" plain>{{ this.followButtonText }}</el-button>
    </div>
  </div>
</template>

<script>
import $ from '../libs/util';

export default {
  name: "followee",
  inject: ['reload'],
  data() {
    return{
      unFollowUserButton: 'display: inline; margin-left: 20px;',
      followButtonText: '取消关注'
    }
  },
  props: {
    info: Object
  },
  methods: {
    unFollowTheUser(){
      const _this = this;
      //点击取关该粉丝用户
      $.ajax.get(
          '/unfollowUserWebby',
          {
            params: {
              userId: _this.info.user.id
            },
            headers: {
              "Authorization": localStorage.getItem("token")
            }
          }).then(res => {
        if (res.data.code === "my200") {
          _this.reload();
          _this.$message({
            message: '已成功取消关注~',
            type: 'success'
          });
        } else if (res.data.code === "my402") {
          //服务器异常，请联系管理员
          _this.$message({
            message: '服务器异常，请联系管理员',
            type: 'error'
          });
        } else {
          //token有问题，1、清空原token与原user；2、进入登陆页面；3、重定向回当前页面
          _this.$store.commit("REMOVE_TOKEN");
          _this.$store.commit("REMOVE_USER");
          _this.$router.push({path:'/loginWebby', query:{next: "/indexWebby"}});
        }
      }).catch(error => {
        console.log(error);
        _this.$message({
          message: '服务器异常，请联系管理员',
          type: 'error'
        });
      })
    }
  }
}
</script>

<style scoped>
img#UserImg{
  position: relative;
  display: inline-block;
  width: 70px;
  height: 70px;
  margin-top: 1.2%;
  margin-left: 1.5%;
  margin-right: 1.5%;
}
</style>
