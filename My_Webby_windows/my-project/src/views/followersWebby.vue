<template>
  <div>
    <div class="followersListNew">
      <div style="margin-top: 1%;">
        <span style="color: rgba(228, 67, 216, 0.6); font-weight: bold; white-space: pre;">{{ "         " + this.currentUser.name }}</span>
        <span style="font-weight: bold; color: rgba(228, 67, 216, 0.6);">的粉丝列表：</span>
        <span style="font-weight: bold; color: rgba(228, 67, 216, 0.6);">{{ this.total }}</span>
        <span style="font-weight: bold; color: rgba(228, 67, 216, 0.6);">人</span>
        <el-divider></el-divider>
      </div>
      <Follower v-for="item in followerList" :info="item" :key="item.id" class="oneFollower"></Follower>
      <el-pagination class="mpage"
                     background
                     layout="prev, pager, next"
                     :current-page="this.currentPage"
                     :page-size="this.pageSize"
                     :total="this.total"
                     @current-change=this.getFollowersPageRes
                     v-if="this.total > 0">
      </el-pagination>
    </div>
  </div>
</template>

<script>
import MyHeader from '../components/myheader';
import MyQuestion from "../components/question";
import MyMessage from "../components/sendMessage";
import Follower from "../components/follower";

import $ from '../libs/util';

export default {
  name: "followersWebby",
  components: {
    MyHeader,
    MyQuestion,
    MyMessage,
    Follower
  },
  data(){
    return{
      followerList: [],
      currentUser: '',
      currentPage: 1,   //当前页码
      total: 0,   //总粉丝数量
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
    getFollowersPageRes(currentPage) {
      const _this = this;
      $.ajax.get(
        '/userWebby/' + _this.$route.params.id + '/followers',
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
            _this.followerList = result.followerList;
            _this.total = result.followerCount;
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
            _this.$router.push({path:'/loginWebby', query:{next: "/userWebby/" + _this.$route.params.id + "/followers" }});
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
    this.getFollowersPageRes(1);
  }
}
</script>

<style scoped>
.mpage{
  text-align: right;
  padding-right: 250px;
  margin-bottom: 20px;
}
.followersListNew {
  position: relative;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  width: 2150px;
  height: 1100px;
  overflow-x: auto;
  overflow-y: auto;
  padding-bottom: 1.5%;
}
.oneFollower{
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
.oneFollower:hover{
  -webkit-transform: perspective(1000px) translate3d(0,0,21px);
  transform: perspective(1000px) translate3d(0,0,21px);
  background: rgba(255,255,255,0.3);
}
</style>
