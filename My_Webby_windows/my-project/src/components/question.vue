<template>
  <div>
    <p style="background: linear-gradient(to bottom, #b3d4fc,rgb(37,187,155)); height: 40px; margin-top: 0; margin-bottom: 0;">
      <span style="color: white; margin-left: 20px; font-size: 18px; font-style: italic; position: relative; top: 7px">提问</span>
      <span class="iconfont icon-quxiao iconCancel" @click="cancelQuestion"></span>
    </p>
    <label for="questionTitle" style="display: block; position: relative; top: 15px; left: 20px">questionTitle :
      <input type="text" id="questionTitle" name="questionTitle" placeholder="please write your question title~" required="required" style="display: block" v-model="questionTitle">
    </label>
    <label for="questionDetail" style="display: block; position: relative; top: 35px; left: 20px">questionDetail :
      <textarea id="questionDetail" name="questionDetail" placeholder="please write your question detail~" required="required" style="display: block" v-model="questionDetail"></textarea>
    </label>
    <button id="cancel" @click="cancelQuestion">cancel</button>
    <button id="commit" @click="commitQuestion">
      commit
      <span class="iconfont icon-fabu iconCommit"></span>
    </button>
  </div>
</template>

<script>
import $post from '../libs/utilPost';
import bus from '../libs/bus';

export default {
  name: "question",
  data(){
    return{
      questionTitle: '',
      questionDetail: '',
    }
  },
  //注入reload方法
  inject:['reload'],
  methods: {
    cancelQuestion(){
      //清空内容
      this.questionTitle = '';
      this.questionDetail = '';

      /*
      * 不可直接  this.$store.commit('setQuestionWindow', false); 了事
      * 否则下一次的提问，需要点击两次提问按钮，才会出现提问弹窗
      * 因为上一次的myheader.vue组件里，isShowQuestionWindow本身为true，点一次，false了，再点一次才能为true
      * 应该调用兄弟组件myheader.vue的handleQuestion()方法
      * https://www.cnblogs.com/PiaoYu/p/11386373.html
      * */
      bus.$emit("closeQuestion");
    },
    commitQuestion(){
      if(this.questionTitle === ''){
        this.questionTitle = "question title please, master~";
      }
      if(this.questionDetail === ''){
        this.questionDetail = "question context please, master~";
      }

      let params = {
        title: this.questionTitle,
        content: this.questionDetail
      }

      const _this = this;

      $post.ajax.post(
        '/questionWebby/add',
        JSON.stringify(params),
        {
          headers:{
            'Content-Type': 'application/json',
            "Authorization": localStorage.getItem("token")
          }
        }
      ).then (res => {
          if (res.data.code === "my200") {
            //添加问题成功
            _this.questionTitle = "commit question success, master~";
            //这里要不要停留一下，不然上面就看不到了
            bus.$emit("closeQuestion");
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
        }).catch (error => {
          console.log(error);
          _this.questionTitle = "someOther fail...";
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
input#questionTitle{
  position: relative;
  top: 10px;
  width: 550px;
  height: 30px;
  font-size: 16px;
  font-style: italic;
}
textarea#questionDetail{
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
