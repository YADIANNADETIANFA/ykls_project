<template>
  <div @click="toConversationDetail">
    <router-link class="imgRouter" :to="'/userWebby/' + this.info.user.id">
      <img id="myImg" :src=this.$store.state.userHeadImg alt="用户头像">
    </router-link>
    <div class="conversation">
      <router-link style="color: #999999; font-size: 14px; text-decoration: none" :to="'/userWebby/' + this.info.user.id">{{ this.info.user.name }}</router-link>
      <p style="margin-top: 5px; margin-bottom: 5px">{{ this.info.message.content }}</p>
    </div>
    <div class="assistMsg">
      <p style="margin-top: 25px; margin-bottom: 5px; font-size: 14px; color: #999999">{{ this.Mydate }}</p>
      <p style=" margin-top: 5px; color: #42b983; font-size: 14px">共{{ this.info.message.id }}条会话</p>
    </div>
  </div>
</template>

<script>

export default {
  name: "conversation",
  props:{
    info: Object
  },
  methods:{
    toConversationDetail(){
      this.$router.push({path:'/msgWebby/detail', query:{conversationId: this.info.message.conversationId}});
    }
  },
  computed:{
    Mydate: function () {
      let timeDate = new Date(this.info.message.createdDate);   /*info是不可以的，必须使用this.info*/
      let Y = timeDate.getFullYear() + '-';
      let M = (timeDate.getMonth() + 1 < 10 ? '0' + (timeDate.getMonth() + 1) : timeDate.getMonth() + 1) + '-';
      let D = timeDate.getDate() + ' ';
      let h = timeDate.getHours() + ':';
      let m = timeDate.getMinutes() + ':';
      let s = timeDate.getSeconds();
      return Y + M + D + h + m + s;
    }
  }
}
</script>re

<style scoped>
img#myImg{
  position: relative;
  top: 20px;
  left: 20px;
  width: 60px;
  height: 60px;
  border-style: solid;
  border-width: 1px;
  border-color: darkgray;
  display: inline-block;
}
.imgRouter{
  display: inline-block;
  height: 100px;
  width: 100px;
}
.conversation{
  display: inline-block;
  width: 80%;
}
.assistMsg{
  display: inline-block;
  float: right;
  margin-right: 10px;
}
</style>
