<template>
  <div>
    <div class="conversationList">
      <el-timeline style="width: 95%">
        <el-timeline-item :timestamp="commentDate(item)" placement="top" v-for="item in this.getConversationList" :key="item.id">
<!--    VUE中报错：ERROR IN RENDER: TYPEERROR: CANNOT READ PROPERTY NAME(或者其他属性) OF UNDEFINED解决办法   https://www.freesion.com/article/80191021817/    -->
          <div v-if="item.user" @click="toConversationDetail(item)" class="oneMes">
            <el-card>
              <div style="display: inline-block; width: 95%;">
                <!--      展示未读消息数？        -->
                <img id="myImg" :src="item.user.headUrl" alt="用户头像">
                <div style="display: inline-block; position: relative; left: 20px; vertical-align: top; width: 1600px;">
                  <h4 style="margin-top: 0;">{{item.user.name}}</h4>
                  <p>{{item.message.content}}</p>
                </div>
              </div>
              <div style="display: inline-block; position: relative; left: 1%; vertical-align: top; margin-top: 1%">
                <p style="color: #42b983; font-size: 14px">共{{ item.message.id }}条会话</p>
              </div>
            </el-card>
          </div>
        </el-timeline-item>

        <el-pagination class="mpage"
                       background
                       layout="prev, pager, next"
                       :current-page="this.currentPage"
                       :page-size="this.pageSize"
                       :total="this.total"
                       @current-change=this.getConversationPageRes
                       v-if="this.total > 0">
        </el-pagination>
      </el-timeline>

    </div>
  </div>
</template>

<script>
import MyHeader from "../components/myheader";
import MyConversation from "../components/conversation";
import MyQuestion from "../components/question";
import MyMessage from "../components/sendMessage";

import $ from '../libs/util';

export default {
  name: "msgListWebby",
  components: {
    MyHeader,
    MyConversation,
    MyQuestion,
    MyMessage
  },
  data(){
    return{
      getConversationList: [],
      currentPage: 1,   //当前页码
      total: 0,   //总私信数量
      pageSize: 10    //每页大小
    }
  },
  computed:{
    showQuestion(){
      return this.$store.state.questionWindow;
    },
    showMessage(){
      return this.$store.state.messageWindow;
    },
    //computed无法直接传参，所以使用闭包函数？
    commentDate() {
      return function(item) {
        //todo:这个时间是最近时间吗？
        let timeDate = new Date(item.message.createdDate);
        let Y = timeDate.getFullYear() + '-';
        let M = (timeDate.getMonth()+1 < 10 ? '0'+(timeDate.getMonth()+1) : timeDate.getMonth()+1) + '-';
        let D = timeDate.getDate() + ' ';
        let h = timeDate.getHours() + ':';
        let m = timeDate.getMinutes() + ':';
        let s = timeDate.getSeconds();
        return Y+M+D+h+m+s;
      }
    }
  },
  methods: {
    toConversationDetail(item){
      this.$router.push({path:'/msgWebby/detail', query:{conversationIdFrom: item.message.conversationId}});
    },
    getConversationPageRes(currentPage) {
      const _this = this;
      $.ajax.get(
        '/msgWebby/list',
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
          _this.getConversationList = result.conversations;
          _this.total = result.conversationCount;
        } else if (res.data.code === "my402") {
          //服务器异常，请联系管理员
          _this.$message({
            message: '会话列表获取异常，请联系管理员',
            type: 'error'
          });
        } else {
          //token有问题，1、清空原token；2、进入登陆页面；3、重定向回当前页面
          _this.$store.commit("REMOVE_TOKEN");
          _this.$store.commit("REMOVE_USER");
          _this.$router.push({path:'/loginWebby', query:{next: "/msgWebby/list"}});
        }
      }).catch(error => {
        console.log(error);
        //服务器异常，请联系管理员
        _this.$message({
          message: '会话列表获取异常，请联系管理员',
          type: 'error'
        });
      })
    }
  },
  mounted(){
    this.getConversationPageRes(1);
  }
}
</script>

<style scoped>
.mpage{
  text-align: right;
}
.conversationList{
  position: relative;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  width: 2150px;
  height: 1100px;
  overflow-x: auto;
  overflow-y: auto;
  padding-top: 1.5%;
  padding-bottom: 1.5%;
}
img#myImg{
  position: relative;
  display: inline-block;
  width: 60px;
  height: 60px;
  border-style: solid;
  border-width: 1px;
  border-color: darkgray;
}
.oneMes{
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
