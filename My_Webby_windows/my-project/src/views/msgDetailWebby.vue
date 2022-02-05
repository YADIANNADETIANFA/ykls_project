<template>
  <div>
    <div class="infinite-list" v-infinite-scroll="load"
         style="position: relative; box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
         width: 2150px; height: 1100px; overflow-x: auto; overflow-y: auto; padding-bottom: 60px;">

      <MyEveryTalk v-for="item in getConversationDetail"
                   :info="item" :key="item.id"
                   style="position: relative; left: 5%; width: 85%; margin-bottom: 30px;"></MyEveryTalk>
    </div>
  </div>
</template>

<script>
import MyHeader from "../components/myheader";
import MyQuestion from "../components/question";
import MyMessage from "../components/sendMessage";
import MyEveryTalk from "../components/everyTalk";

import $ from '../libs/util';

export default {
  name: "msgDetailWebby",
  components:{
    MyHeader,
    MyEveryTalk,
    MyQuestion,
    MyMessage
  },
  data(){
    return{
      getConversationDetail: [],    //所有的对话记录
      totalNum: '', //本组对话的总对话量
      everyTimeDetail: []   //每次触发滚动条load时，新获取到的对话记录
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
  methods :{
    load () {
      //只有在前台未获取到全部对话数据时，滑动滚动条才会继续向后台进行请求获取
      if (this.getConversationDetail.length < this.totalNum) {
        this.getTalkDatas(this.getConversationDetail.length);
      }
    },
    getTalkDatas(num) {
      const _this = this;
      $.ajax.get(
        '/msgWebby/detail',
        {
          headers: {
            "Authorization": localStorage.getItem("token")
          },
          params: {
            conversationId: _this.$route.query.conversationIdFrom,
            beginIndex: num   //请求对话的起始位置，即偏移量
          }
        }).then(res => {
        if (res.data.code === "my200") {
          let result = JSON.parse(res.data.data);
          _this.everyTimeDetail = result.conversation;
          _this.totalNum = result.totalNum;
          _this.getConversationDetail = _this.getConversationDetail.concat(_this.everyTimeDetail);
          _this.everyTimeDetail = [];
        } else if (res.data.code === "my402") {
          //服务器异常，请联系管理员
          _this.$message({
            message: '会话获取异常，请联系管理员',
            type: 'error'
          });
          _this.getConversationDetail = [];
          _this.everyTimeDetail = [];
        } else {
          _this.getConversationDetail = [];
          _this.everyTimeDetail = [];
          //token有问题，1、清空原token；2、进入登陆页面；3、重定向回当前页面
          _this.$store.commit("REMOVE_TOKEN");
          _this.$store.commit("REMOVE_USER");
          _this.$router.push({path:'/loginWebby', query:{next: "/msgWebby/list"}});
        }
      }).catch(error => {
        console.log(error);
        _this.getConversationDetail = [];
        _this.everyTimeDetail = [];
        //服务器异常，请联系管理员
        _this.$message({
          message: '会话获取异常，请联系管理员',
          type: 'error'
        });
      })
    }
  },
  mounted(){
    this.getTalkDatas(0);
  }
}
</script>

<style scoped>
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

</style>
