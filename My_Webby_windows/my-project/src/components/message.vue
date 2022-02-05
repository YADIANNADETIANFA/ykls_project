<template>
  <div>
    <router-link class="imgRouter" :to="'/userWebby/' + this.info.userId">
      <img id="myImg" :src="this.info.headUrl" alt="用户头像">
    </router-link>
    <div class="imgRight">
      <div id="quesTitle">
        <router-link :to="'/questionWebby/' + this.info.questionId" style="text-decoration: none">{{this.info.questionTitle}}</router-link>
      </div>
      <div id="userId">
        <router-link :to="'/userWebby/' + this.info.userId" style="text-decoration: none">{{this.info.userName + ","}}</router-link>
        <span>{{ this.Mydate }}</span>
      </div>
      <span style="display: block; position: relative; top: 15px; left: 15px">{{this.info.questionDescription}}</span>
      <div id="toQuesDetail">
        <router-link :to="'/questionWebby/' + this.info.questionId" style="text-decoration: none">查看问题详情</router-link>
      </div>
      <div id="quesCommentCount">
        <router-link :to="'/questionWebby/' + this.info.questionId" style="text-decoration: none">{{this.info.questionCommentCount}}条评论</router-link>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "message",
  props:{
    info: Object
  },
  computed:{
    Mydate: function (){
      let timeDate = new Date(this.info.questionCreatedDate);   /*info是不可以的，必须使用this.info*/
      let Y = timeDate.getFullYear() + '-';
      let M = (timeDate.getMonth()+1 < 10 ? '0'+(timeDate.getMonth()+1) : timeDate.getMonth()+1) + '-';
      let D = timeDate.getDate() + ' ';
      let h = timeDate.getHours() + ':';
      let m = timeDate.getMinutes() + ':';
      let s = timeDate.getSeconds();
      return Y+M+D+h+m+s;
    }
  }
}
</script>

<style scoped>
img#myImg{
  position: relative;
  width: 60px;
  height: 60px;
  border-style: solid;
  border-width: 1px;
  border-color: darkgray;
  display: inline;
}
div#quesTitle{
  display: inline-block;
  position: relative;
  left: 15px;
  top: 5px;
  width: 100%;
  height: 25px;
}
div#userId{
  position: relative;
  display: block;
  left: 15px;
  top: 5px;
}
div#toQuesDetail{
  display: inline-block;
  position: relative;
  top: 30px;
  left: 15px;
}
div#quesCommentCount{
  display: inline-block;
  position: relative;
  top: 30px;
  left: 25px;
}
.imgRouter{
  position: relative;
  display: inline-block;
  width: 60px;
  height: 60px;
  left: 10px;
  top: 10px;
}
.imgRight{
  display: inline-block;
  vertical-align: top;  /* inline-block,顶部对齐 */
  position: relative;
  top: 10px;
  left: 10px;
  width: 80%;
  height: 90%;
}
</style>
