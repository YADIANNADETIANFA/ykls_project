<template>
  <div>
    <img id="UserImg" :src=this.$store.state.userHeadImg :alt="'痞老板未显示出来'">
    <div style="display: inline-block; margin-top: 10px">
      <span>{{ this.userName }}</span>
      <span>{{ this.context }}</span>
      <span>{{ this.commentDate }}</span>
      <p>{{ this.questionTitle }}</p>
    </div>
  </div>
</template>

<script>
export default {
  name: "everyPullPushMsg",
  props:{
    info: Object
  },
  data(){
    return{
      userHead: '',
      operationId: '',
      questionTitle: '',
      userName: '',
      userId: ''
    }
  },
  computed: {
    commentDate: function(){
      let timeDate = new Date(this.info.feed.createdDate);   /*info是不可以的，必须使用this.info*/
      let Y = timeDate.getFullYear() + '-';
      let M = (timeDate.getMonth()+1 < 10 ? '0'+(timeDate.getMonth()+1) : timeDate.getMonth()+1) + '-';
      let D = timeDate.getDate() + ' ';
      let h = timeDate.getHours() + ':';
      let m = timeDate.getMinutes() + ':';
      let s = timeDate.getSeconds();
      return Y+M+D+h+m+s;
    },
    context: function(){
      if(this.info.feed.type === 4){
        return "关注了该问题，"
      }else if(this.info.feed.type === 1){
        return "评论了该问题，"
      }
    }
  },
  mounted() {
      let content = JSON.parse(this.info.feed.data);
      this.userHead = content.userHead;
      this.questionId = content.questionId;
      this.questionTitle = content.questionTitle;
      this.userName = content.userName;
      this.userId = content.userId;
  }
}
</script>

<style scoped>
img#UserImg{
  position: relative;
  display: inline-block;
  width: 70px;
  height: 70px;
  margin-top: 13px;
  margin-left: 15px;
  margin-right: 20px;
  float: left;
}
</style>
