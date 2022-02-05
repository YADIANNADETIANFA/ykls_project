<template>
  <div>
    <div class="bottomsPart">
      <button id="buttonCommentUp" class="commentUp" :class="{'originalLikeStatus': this.originalCommentUp}" ref="refCommentUp" @click="commentUp">
        <span class="iconfont icon-dianzan"></span>
        <p style="margin-top: 0; margin-bottom: 0">{{ this.c_likeCount }}</p>
      </button>
      <button id="buttonCommentDown" class="commentDown" :class="{'originalLikeStatus': this.originalCommentDown}" ref="refCommentDown" @click="commentDown">
        <span class="iconfont icon-cai"></span>
      </button>
    </div>
    <div class="commentPart">
      <p style="margin-top: 5px; margin-bottom: 5px">{{ info.user["name"] }}</p>
      <p style="margin-top: 5px; margin-bottom: 5px">{{ info.comment["content"] }}</p>
      <p style="display: inline-block; margin-top: 15px; margin-bottom: 5px">发布于</p>
      <p style="display: inline-block">{{ this.commentDate }}</p>
    </div>
    <img id="commentUserImg" :src=this.$store.state.userHeadImg :alt="'痞老板未显示出来'">
  </div>
</template>

<script>
import $ from '../libs/util';

export default {
  name: "comment",
  props:{
    info: Object
  },
  data: function() {
    return {
      likeCount: -1
    }
  },
  methods: {
    commentUp(){

      const _this = this;

      //点赞
      $.ajax.get(
        '/likeWebby',
        {
          params: {
            commentId: _this.info.comment["id"]
          },
          headers:{
            "Authorization": localStorage.getItem("token")
          }
        }).then(res => {
          if (res.data.code === "my200") {
            let result = JSON.parse(res.data.data);
            _this.likeCount = result.likeCount;
            _this.$refs.refCommentUp.style.borderWidth = '3px';
            _this.$refs.refCommentUp.style.borderColor = 'black';
            _this.$refs.refCommentDown.style.borderWidth = '1px';
            _this.$refs.refCommentDown.style.borderColor = 'aquamarine';
          } else if (res.data.code === "my402") {
            //alert先撑一会
            //已经赞过，或服务器异常，请联系管理员
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
    },
    commentDown(){

      const _this = this;

      //点踩
      $.ajax.get(
        '/dislikeWebby',
        {
          params: {
            commentId: this.info.comment["id"]
          },
          headers:{
            "Authorization": localStorage.getItem("token")
          }
        }).then(res => {
          if (res.data.code === "my200") {
            let result = JSON.parse(res.data.data);
            _this.likeCount = result.likeCount;
            _this.$refs.refCommentUp.style.borderWidth = '1px';
            _this.$refs.refCommentUp.style.borderColor = 'aquamarine';
            _this.$refs.refCommentDown.style.borderWidth = '3px';
            _this.$refs.refCommentDown.style.borderColor = 'black';
          } else if (res.data.code === "my402") {
            //alert先撑一会
            //已经赞过，或服务器异常，请联系管理员
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
  },
  computed: {
    commentDate: function(){
      let timeDate = new Date(this.info.comment["createdDate"]);   /*info是不可以的，必须使用this.info*/
      let Y = timeDate.getFullYear() + '-';
      let M = (timeDate.getMonth()+1 < 10 ? '0'+(timeDate.getMonth()+1) : timeDate.getMonth()+1) + '-';
      let D = timeDate.getDate() + ' ';
      let h = timeDate.getHours() + ':';
      let m = timeDate.getMinutes() + ':';
      let s = timeDate.getSeconds();
      return Y+M+D+h+m+s;
    },
    c_likeCount: function (){
      if(this.likeCount === -1){
        return this.info.likeCount;
      }else{
        return this.likeCount;
      }
    },
    //也可以不用v-bind绑定class的方法，因为ref可以直接在mounted中改变CSS样式
    //https://blog.csdn.net/tan1071923745/article/details/81162667
    //这里只是为了尝试一下不同的方法
    originalCommentUp: function(){
      if(this.info.liked === 1){
        return true;
      }else{
        return false;
      }
    },
    originalCommentDown: function(){
      if(this.info.liked === -1){
        return true;
      }else{
        return false;
      }
    }
  }
}
</script>

<style scoped>
.commentUp{
  position: relative;
  top: 10px;
  left: 10px;
  background-color: lightgreen;
  border-style: solid;
  border-width: 1px;
  border-color: aquamarine;
}
.commentDown{
  position: relative;
  top: 15px;
  left: 10px;
  background-color: thistle;
  border-style: solid;
  border-width: 1px;
  border-color: aquamarine;
}
/* 必须放在commentUp和commentDown之后，CSS也是有顺序的，会覆盖 */
.originalLikeStatus{
  border-width: 3px;
  border-color: black;
}
.bottomsPart{
  position: relative;
  display: inline-block;
  width: 50px;
  height: 80px;
  top: -20px;
  left: 10px;
}
.commentPart{
  position: relative;
  margin-left: 40px;
  display: inline-block;
  width: 60%;
}
img#commentUserImg{
  position: relative;
  display: inline-block;
  width: 40px;
  height: 40px;
  margin-top: 10px;
  margin-right: 10px;
  float: right;
}
</style>
