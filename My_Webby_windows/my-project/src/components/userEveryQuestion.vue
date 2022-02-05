<template>
  <div>
    <p style="margin-left: 20px; margin-top: 5px; margin-bottom: 10px">{{ this.info.questionTitle }}</p>
    <span style="margin-left: 20px;">{{ this.info.userName }},</span>
    <span>{{ this.commentDate }}</span>
    <p style="margin-left: 20px; margin-top: 5px; margin-bottom: 10px">{{ this.info.questionContent }}</p>
    <button @click="followTheQuestion" style="border: none; position: relative; left: 8px; color: #0077aa; outline: none" ref="followQuestionRef">{{ this.followQuestionText }}</button>
    <span style="margin-left: 10px; font-style: italic; font-size: small; color: #795da3">({{ this.followQuestionCount }}人已关注该问题)</span>
    <router-link :to="{path:'/questionWebby/'+ this.info.questionId}" style="text-decoration: none;">
      <span class="iconfont icon-pinglun" style="margin-left: 20px; white-space: pre; color: #0077aa">  {{ this.info.questionCommentCount }}条评论</span>
    </router-link>
  </div>
</template>

<script>
import $ from '../libs/util';

export default {
  name: "userEveryQuestion",
  inject: ['reload'],
  data() {
    return{
      followQuestionText: '',
      followQuestionCount: ''   //这样做页面不会闪烁
    }
  },
  props:{
    info: Object
  },
  computed: {
    commentDate: function(){
      let timeDate = new Date(this.info.questionCreatedDate);   /*info是不可以的，必须使用this.info*/
      let Y = timeDate.getFullYear() + '-';
      let M = (timeDate.getMonth()+1 < 10 ? '0'+(timeDate.getMonth()+1) : timeDate.getMonth()+1) + '-';
      let D = timeDate.getDate() + ' ';
      let h = timeDate.getHours() + ':';
      let m = timeDate.getMinutes() + ':';
      let s = timeDate.getSeconds();
      return Y+M+D+h+m+s;
    }
  },
  methods: {
    followTheQuestion(){

      const _this = this;

      if(this.followQuestionText === '已关注'){
        //点击取消关注该问题
        $.ajax.get(
          '/unfollowQuestionWebby',
          {
            params: {
              questionId: _this.info.questionId
            },
            headers: {
              "Authorization": localStorage.getItem("token")
            }
          }).then(res => {
            if (res.data.code === "my200") {
              _this.$refs.followQuestionRef.style.color = '#0077aa';
              _this.followQuestionText = '+关注问题';
              _this.followQuestionCount -= 1;  //这样做页面不会闪烁
            } else if (res.data.code === "my402") {
              //alert先撑一会
              //服务器异常，请联系管理员
              alert(res.data.msg)
            } else {
              //token有问题，1、清空原token与原user；2、进入登陆页面；3、重定向回当前页面
              _this.$store.commit("REMOVE_TOKEN");
              _this.$store.commit("REMOVE_USER");
              _this.$router.push({path:'/loginWebby', query:{next: "/indexWebby"}});
            }
        }).catch(error => {
          console.log(error);
        })
      } else {
        //点击关注该问题
        $.ajax.get (
          '/followQuestionWebby',
          {
            params:{
              questionId: _this.info.questionId
            },
            headers: {
              "Authorization": localStorage.getItem("token")
            }
          }).then(res => {
            if (res.data.code === "my200") {
              _this.$refs.followQuestionRef.style.color = 'darkgray';
              _this.followQuestionText = '已关注';
              _this.followQuestionCount += 1;  //这样做页面不会闪烁
            } else if (res.data.code === "my402") {
              //alert先撑一会
              //服务器异常，请联系管理员
              alert(res.data.msg);
            } else {
              //token有问题，1、清空原token与原user；2、进入登陆页面；3、重定向回当前页面
              _this.$store.commit("REMOVE_TOKEN");
              _this.$store.commit("REMOVE_USER");
              _this.$router.push({path:'/loginWebby', query:{next: "/indexWebby"}});
            }
        }).catch(error => {
          console.log(error);
        })
      }
    }
  },
  mounted(){
    if(this.info.followed){
      //当前登录用户，已关注该问题
      this.$refs.followQuestionRef.style.color = 'darkgray';
      //个人感觉，这里的‘已关注’，好于‘取消关注’，类似微信的感觉
      this.followQuestionText = '已关注';
    }else{
      //当前登录用户，未关注该问题
      this.$refs.followQuestionRef.style.color = '#0077aa';
      this.followQuestionText = '+关注问题';
    }
    this.followQuestionCount = this.info.followCount;
  }
}
</script>

<style scoped>

</style>
