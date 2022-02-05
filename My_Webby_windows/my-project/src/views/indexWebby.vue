<template>
  <div>
    <div class="messageList">
      <Message v-for="item in getMessageList" :info="item" :key="item.id" class="oneMes"></Message>
      <el-pagination class="mpage"
                     background
                     layout="prev, pager, next"
                     :current-page="this.currentPage"
                     :page-size="this.pageSize"
                     :total="this.total"
                     @current-change=this.getCommentPageRes
                     v-if="this.total > 0">
      </el-pagination>
    </div>
  </div>
</template>

<script>
import Message from "../components/message";    //注意大小写Message
import MyHeader from "../components/myheader";
import MyQuestion from "../components/question";
import MyMessage from "../components/sendMessage";

import $ from '../libs/util';

export default {
  name: "indexWebby",
  components: {
    MyHeader,
    Message,
    MyQuestion,
    MyMessage
  },
  data(){
    return{
      getMessageList: [],
      currentPage: 1,   //当前页码
      total: 0,   //总问题数量
      pageSize: 10    //每页大小
    }
  },
  computed:{
    showQuestion(){
      return this.$store.state.questionWindow;
    },
    showMessage(){
      return this.$store.state.messageWindow;
    }
  },
  methods: {
    getCommentPageRes(currentPage) {
      const _this = this;
      $.ajax.get(
        '/indexWebby',
        {
          params: {
            currentPage: currentPage
          },
          headers: {
            "Authorization": localStorage.getItem("token")
          }
        }
      ).then(res => {
        if (res.data.code === "my200") {
          let result = JSON.parse(res.data.data);
          _this.getMessageList = JSON.parse(result.questions);  //因result.questions在后台是一个jsonArray，这里还要再JSON.parse一次
          _this.total = result.totalQuestionCount;
        } else if (res.data.code === "my402") {
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
  },
  mounted(){
    this.getCommentPageRes(1);
  }

}
</script>

<style scoped>
.mpage{
  text-align: right;
  padding-right: 250px;
  margin-bottom: 20px;
}
.myQuestion{
  position: fixed;
  top: 30%;
  left: 37%;
  width: 600px;
  height: 300px;
  border-style: solid;
  border-width: 2px;
  border-color: #666666;
  background-color: #fffdef;
  border-radius: 6px;

}
.myMessage{
  position: fixed;
  top: 30%;
  left: 37%;
  width: 600px;
  height: 300px;
  border-style: solid;
  border-width: 2px;
  border-color: #666666;
  background-color: #fffdef;
  border-radius: 6px;
}
.messageList{
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  top: 8%;
  left: 8%;
  width: 2150px;
  height: 1200px;
  overflow-x: auto;
  overflow-y: scroll;
  z-index: -1;
  padding-top: 20px;
}
.oneMes{
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  position: relative;
  left: 5%;
  width: 85%;
  height: 160px;
  overflow-x: auto;
  margin-bottom: 20px;
  overflow-y: auto;

  /*用于鼠标悬浮动画*/
  -webkit-transition: -webkit-transform 0.35s;
  transition: transform 0.35s;
  -webkit-transform: perspective(1000px) translate3d(0,0,0);
}
/*用于鼠标悬浮动画*/
.oneMes:hover{
  -webkit-transform: perspective(1000px) translate3d(0,0,21px);
  transform: perspective(1000px) translate3d(0,0,21px);
  background: rgba(255,255,255,0.3);
  cursor:pointer;
}
</style>
