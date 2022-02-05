<template>
  <div>
    <p style="background: linear-gradient(to bottom, #b3d4fc,rgb(37,187,155)); height: 40px; margin-top: 0; margin-bottom: 0;">
      <span style="color: white; margin-left: 20px; font-size: 18px; font-style: italic; position: relative; top: 7px">发送私信</span>
      <span class="iconfont icon-quxiao iconCancel" @click="cancelMsg"></span>
    </p>
    <label for="msgTarget" style="display: block; position: relative; top: 15px; left: 20px">sendTo :
      <input type="text" id="msgTarget" name="msgTarget" placeholder="please select your target~" required="required" style="display: block" v-model="msgTarget">
    </label>
    <label for="msgContext" style="display: block; position: relative; top: 35px; left: 20px">context :
      <textarea id="msgContext" name="msgContext" placeholder="please write your message context~" required="required" style="display: block" v-model="msgContext"></textarea>
    </label>
    <button id="cancel" @click="cancelMsg">cancel</button>
    <button id="commit" @click="commitMsg">
      commit
      <span class="iconfont icon-fabu iconCommit"></span>
    </button>
  </div>
</template>

<script>
import $post from '../libs/utilPost';
import bus from '../libs/bus';

export default {
  name: "sendMessage",
  inject: ['reload'],
  data(){
    return{
      msgTarget: '',
      msgContext: '',
    }
  },
  methods: {
    cancelMsg(){
      //清空内容
      this.msgTarget = '';
      this.msgContext = '';
      bus.$emit("closeMessage");

    },
    commitMsg(){

      if (this.msgTarget === '') {
        this.msgTarget = "msg target please, master~";
      }
      if (this.msgContext === '') {
        this.msgContext = "msg context please, master~";
      }

      let params = {
        toName: this.msgTarget,
        content: this.msgContext
      }

      const _this = this;

      $post.ajax.post(
        '/msgWebby/addMessage',
        JSON.stringify(params),
        {
          headers:{
            'Content-Type': 'application/json',
            "Authorization": localStorage.getItem("token")
          }
        }
      ).then(res => {
        if (res.data.code === "my200") {

          //发送消息成功
          _this.msgTarget = "commit msg success, master~";
          //考虑暂停一下，不然上面的东西看不到，或者alert？
          bus.$emit("closeMessage");
          //或许更好的是，重新ajax请求一下，而不是整个刷新页面？
          _this.reload();
        } else if (res.data.code === "my402") {
          //alert先撑一会
          //服务器异常，请联系管理员
          alert(res.data.msg);
        } else {
          //token有问题，1、清空原token；2、进入登陆页面；3、重定向回当前页面
          _this.$store.commit("REMOVE_TOKEN");
          _this.$store.commit("REMOVE_USER");
          _this.$router.push({path:'/loginWebby', query:{next: "/indexWebby"}});
        }
      }).catch(error => {
          console.log(error);
          _this.msgTarget = "someOther fail...";
        });
    }
  }
}
</script>

<style scoped>
.iconCancel{
  color: white;
  float: right;
  font-size: 20px;
  position: relative;
  top: 8px;
  right: 10px;
}
.iconCommit{
  color: white;
  float: right;
  font-size: 20px;
  position: relative;
  top: -2px;
  left: -2px;
}
input#msgTarget{
  position: relative;
  top: 10px;
  width: 550px;
  height: 30px;
  font-size: 16px;
  font-style: italic;
}
textarea#msgContext{
  position: relative;
  top: 10px;
  /*固定写死大小暂时。。。不会调好它的高度和外层高度的自适应变化*/
  min-height: 70px;
  max-height: 70px;
  min-width: 550px;
  max-width: 550px;
  font-size: 16px;
}
button#cancel{
  position: relative;
  top: 60px;
  left: 370px;
  font-size: 16px;
  font-style: italic;
  color: #666666;
  border-style: hidden;
  background-color: rgba(0,0,0,0);
  padding-top: 4px;
  padding-bottom: 7px;
  padding-left: 12px;
  padding-right: 17px;
}
button#commit{
  position: relative;
  top: 60px;
  left: 390px;
  width: 100px;
  border-radius: 4px;
  border-color: rgb(37,187,155);
  background-color: rgba(37,187,155,0.8);
  font-size: 16px;
  font-style: italic;
  padding-top: 4px;
  color: white;
}
</style>

<!-- template下只能有一个根元素 -->
<!-- 线性渐变，但是只能background，不能background-color，否则无效 -->
