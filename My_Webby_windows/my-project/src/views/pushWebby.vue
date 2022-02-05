<template>
  <div>
    <div class="pushListNew">
      <span class="iconfont icon-dongtai5" style="margin-left: 2%; color: rgba(67, 87, 228, 0.6);"></span>
      <span style="font-weight: bold; color: rgba(67, 87, 228, 0.6); white-space: pre">   最新动态</span>
      <el-divider></el-divider>
      <el-timeline style="width: 95%">
        <el-timeline-item :timestamp="commentDate(item)" placement="top" v-for="item in this.pullPushMsgList" :key="item.id">
          <el-card>
            <div style="display: inline-block;">
              <img id="myImg" :src="headUrl(item)" alt="用户头像">
              <div style="display: inline-block; position: relative; left: 20px; vertical-align: top; width: 1800px;">
                <h4 style="margin-top: 0;">{{userName(item)}}</h4>
                <p>{{context(item)}}</p>
                <p>{{questionTile(item)}}</p>
              </div>
            </div>
          </el-card>
        </el-timeline-item>

        <el-pagination class="mpage"
                       background
                       layout="prev, pager, next"
                       :current-page="this.currentPage"
                       :page-size="this.pageSize"
                       :total="this.total"
                       @current-change=this.getPushListPageRes
                       v-if="this.total > 0">
        </el-pagination>
      </el-timeline>
    </div>
  </div>
</template>

<script>
import MyHeader from '../components/myheader';
import MyQuestion from "../components/question";
import MyMessage from "../components/sendMessage";
import MyEveryPullPushMsg from "../components/everyPullPushMsg";

import $ from '../libs/util';

export default {
  name: "pushWebby.vue",
  components: {
    MyHeader,
    MyQuestion,
    MyMessage,
    MyEveryPullPushMsg
  },
  data(){
    return{
      pullPushMsgList: [],
      currentPage: 1,   //当前页码
      total: 0,   //总pushList消息总量
      pageSize: 10    //每页大小
    }
  },
  computed: {
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
        let timeDate = new Date(item.feed.createdDate);
        let Y = timeDate.getFullYear() + '-';
        let M = (timeDate.getMonth()+1 < 10 ? '0'+(timeDate.getMonth()+1) : timeDate.getMonth()+1) + '-';
        let D = timeDate.getDate() + ' ';
        let h = timeDate.getHours() + ':';
        let m = timeDate.getMinutes() + ':';
        let s = timeDate.getSeconds();
        return Y+M+D+h+m+s;
      }
    },
    headUrl() {
      return function(item) {
        return JSON.parse(item.feed.data).userHead;
      }
    },
    context() {
      return function (item) {
        if (item.feed.type === 4) {
          return "关注了该问题博客";
        } else if (item.feed.type === 1) {
          return "评论了该问题博客";
        }
      }
    },
    userName() {
      return function (item) {
        return JSON.parse(item.feed.data).userName;
      }
    },
    questionTile() {
      return function(item) {
        return JSON.parse(item.feed.data).questionTitle;
      }
    }
  },
  methods: {
    getPushListPageRes(currentPage) {
      const _this = this;
      $.ajax.get(
        '/pushfeedsWebby',
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
          _this.pullPushMsgList = result.feeds;
          _this.total = result.count;
        } else if (res.data.code === "my402") {
          //服务器异常，请联系管理员
          _this.$message({
            message: 'push动态列表获取异常，请联系管理员',
            type: 'error'
          });
        } else {
          //token有问题，1、清空原token；2、进入登陆页面；3、重定向回当前页面
          _this.$store.commit("REMOVE_TOKEN");
          _this.$store.commit("REMOVE_USER");
          _this.$router.push({path:'/loginWebby', query:{next: "/pushfeeds"}});
        }
      }).catch(error => {
        console.log(error);
        //服务器异常，请联系管理员
        _this.$message({
          message: 'push动态列表获取异常，请联系管理员',
          type: 'error'
        });
      })
    }
  },
  mounted(){
    this.getPushListPageRes(1);
  }
}
</script>

<style scoped>
.mpage{
  text-align: right;
  padding-right: 250px;
  margin-bottom: 20px;
}
.pushListNew{
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
  z-index: 2;
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
  z-index: 2;
}
.pullPushMsgPart{
  position: relative;
  top: 80px;
  left: 640px;
  width: 1000px;
  height: 1050px;
  border-style: solid;
  border-width: 2px;
  border-color: #b3d4fc;
  overflow-y: auto;
  padding-bottom: 20px;
}
.everyPullPushMsg{
  position: relative;
  top: 20px;
  left: 20px;
  width: 95%;
  height: 120px;
  border-style: solid;
  border-width: 2px;
  border-color: hotpink;
  overflow-x: auto;
  margin-bottom: 20px;
  overflow-y: auto;
  background-color: rgba(204,204,204,0.2);
}
</style>
