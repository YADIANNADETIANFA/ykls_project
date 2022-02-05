<template>
  <div>
    <div v-if="othersTalk">
    <!--  对方说的话    -->
      <router-link class="imgRouterLeft" :to="'/userWebby/' + this.info.user.id">
        <img id="myImgLeft" :src=this.info.user.headUrl alt="用户头像">
      </router-link>
      <span class="iconfont icon-liaotiankuangzuobian leftTriangle"></span>
      <div class="leftMiddle">
        <p class="leftTalk">{{ this.info.message.content }}</p>
        <p style="color: blueviolet; margin-bottom: 0; margin-top: 0;">{{ Mydate }}</p>
      </div>
    </div>

    <div v-if="!othersTalk">
      <!--  当前登录人说的话    -->
      <div class="rightMiddle">
        <p class="rightTalk">{{ this.info.message.content }}</p>
        <p style="color: blueviolet; margin-bottom: 0; margin-top: 0; text-align: right">{{ Mydate }}</p>
      </div>
      <span class="iconfont icon-liaotiankuangyoubian rightTriangle"></span>
      <router-link class="imgRouterRight" :to="'/userWebby/' + this.info.user.id">
        <img id="myImgRight" :src=this.info.user.headUrl alt="用户头像">
      </router-link>
    </div>
  </div>
</template>

<script>
export default {
  name: "everyTalk",
  props:{
    info: Object
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
    },
    othersTalk: function (){
      if(this.info.user.id !== this.$store.state.user.id){
        return true;
      }else{
        return false;
      }
    }
  }
}
</script>

<style scoped>
img#myImgLeft{
  position: relative;
  width: 50px;
  height: 50px;
  border-style: solid;
  border-width: 1px;
  border-color: darkgray;
}
img#myImgRight{
  position: relative;
  width: 50px;
  height: 50px;
  border-style: solid;
  border-width: 1px;
  border-color: darkgray;
}
.imgRouterLeft{
  position: relative;
  display: inline-block;
  top: 20px;
  left: 20px;
  height: 50px;
  width: 50px;
}
.imgRouterRight{
  position: relative;
  display: inline-block;
  top: 20px;
  left: 190px;
  height: 50px;
  width: 50px;
}
.leftMiddle{
  display: inline-block;
  vertical-align: top;  /* inline-block,顶部对齐 */
  position: relative;
  left: 15px;
  top: 13px;
  width: 80%;
  background-color: rgba(142, 176, 232, 0.4);
  padding-left: 70px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}
.rightMiddle{
  display: inline-block;
  vertical-align: top;  /* inline-block,顶部对齐 */
  position: relative;
  left: 200px;
  top: 13px;
  width: 80%;
  background-color: rgba(74, 222, 192, 0.4);
  padding-right: 70px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}
.leftTriangle{
  position: relative;
  color: rgba(142, 176, 232, 0.4);
  font-size: 22px;
  left: 25px;
  top: -10px;
}
.rightTriangle{
  position: relative;
  color: rgba(74, 222, 192, 0.4);
  font-size: 22px;
  left: 189px;
  top: -10px;
}
.leftTalk{
  display: inline-block;
  position: relative;
  margin-right: 70px;
  text-align: justify;    /* 文本对齐方式 */
}
.rightTalk{
  display: inline-block;
  position: relative;
  margin-left: 70px;
  text-align: justify;    /* 文本对齐方式 */
}
</style>
