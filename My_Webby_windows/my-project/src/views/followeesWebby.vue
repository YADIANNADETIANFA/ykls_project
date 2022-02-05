<template>
  <div>
    <div class="followeesList">
      <div style="margin-top: 1%;">
        <span style="color: rgba(60, 77, 197, 0.6); font-weight: bold; white-space: pre">{{ "         " + this.currentUser.name }}</span>
        <span style="font-weight: bold; color: rgba(60, 77, 197, 0.6);">关注了:</span>
        <span style="font-weight: bold; color: rgba(60, 77, 197, 0.6);">{{ this.total }}</span>
        <span style="font-weight: bold; color: rgba(60, 77, 197, 0.6);">人</span>
        <el-divider></el-divider>
      </div>
      <Followee v-for="item in followeeList" :info="item" :key="item.id" class="oneFollowee"></Followee>
      <el-pagination class="mpage"
                     background
                     layout="prev, pager, next"
                     :current-page="this.currentPage"
                     :page-size="this.pageSize"
                     :total="this.total"
                     @current-change=this.getFolloweesPageRes
                     v-if="this.total > 0">
      </el-pagination>
    </div>
  </div>
</template>

<script>
import MyHeader from '../components/myheader';
import MyQuestion from "../components/question";
import MyMessage from "../components/sendMessage";
import Followee from "../components/followee";

import $ from '../libs/util';

export default {
  name: "followeesWebby",
  components: {
    MyHeader,
    MyQuestion,
    MyMessage,
    Followee
  },
  data(){
    return{
      followeeList: [],
      currentUser: '',
      currentPage: 1,   //当前页码
      total: 0,   //总关注数量
      pageSize: 10    //每页大小
    }
  },
  computed: {
    showQuestion(){
      return this.$store.state.questionWindow;
    },
    showMessage(){
      return this.$store.state.messageWindow;
    }
  },
  methods: {
    getFolloweesPageRes(currentPage) {
      const _this = this;
      $.ajax.get(
        '/userWebby/' + _this.$route.params.id + '/followees',
        {
          params: {
            currentPage: currentPage
          },
          headers: {
            "Authorization": localStorage.getItem("token")
          }
        }).then(res => {
        if (res.data.code === "my200") {
          let result = JSON.parse(res.data.data);
          _this.followeeList = result.followeeList;
          _this.total = result.followeeCount;
          _this.currentUser = result.currentUser;
        } else if (res.data.code === "my402") {
          _this.$message({
            message: '服务器异常，请联系管理员',
            type: 'error'
          });
        } else {
          //token有问题，1、清空原token；2、进入登陆页面；3、重定向回当前页面
          _this.$store.commit("REMOVE_TOKEN");
          _this.$store.commit("REMOVE_USER");
          _this.$router.push({path:'/loginWebby', query:{next: "/userWebby/" + _this.$route.params.id + "/followees" }});
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
    this.getFolloweesPageRes(1);
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
.followeesList{
  position: relative;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  width: 2150px;
  height: 1100px;
  overflow-x: auto;
  overflow-y: auto;
  padding-bottom: 1.5%;
}
.oneFollowee{
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  position: relative;
  left: 3%;
  width: 90%;
  height: 120px;
  overflow-x: auto;
  margin-bottom: 20px;
  overflow-y: auto;

  /*用于鼠标悬浮动画*/
  -webkit-transition: -webkit-transform 0.35s;
  transition: transform 0.35s;
  -webkit-transform: perspective(1000px) translate3d(0,0,0);
}
.oneFollowee:hover{
  -webkit-transform: perspective(1000px) translate3d(0,0,21px);
  transform: perspective(1000px) translate3d(0,0,21px);
  background: rgba(255,255,255,0.3);
}
</style>
