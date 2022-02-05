<template>
  <div>
    <div style="height: 100%; position: relative; left: 10%; width: 90%">
      <div class="userBorder">
        <div style="padding-left: 14%;">
          <el-image class="userImg" :src="this.userHeadImg" :preview-src-list="this.srcList"></el-image>
          <div style="display: inline-block; margin-left: 2%">
            <h3 style="margin-top: 10px; vertical-align: top;">{{ this.user.name }}</h3>
            <span style="color: cornflowerblue">{{ this.followerCount }}</span>
            <span>粉丝/</span>
            <span style="color: cornflowerblue">{{ this.followeeCount }}</span>
            <span>关注/</span>
            <span style="color: cornflowerblue">{{ this.commentCount }}</span>
            <span>回答/</span>
            <span style="color: cornflowerblue">548（待修改）</span>
            <span>赞同</span>
          </div>
          <div style="display: inline-block; vertical-align: top; position: relative; top: 25px; left: 30%">
            <el-button type="primary" @click="this.followTheUser" :style="this.followUserButton" plain>{{ this.followButtonText }}</el-button>
            <el-button type="info" @click="this.followTheUser" :style="this.unFollowUserButton" plain>{{ this.followButtonText }}</el-button>
          </div>
        </div>

        <el-divider></el-divider>

        <div style="position: relative; left: 10%;">
          <span class="iconfont icon-yuangongmulu"></span>
          <span>最新动态</span>
          <p style="margin-top: 0">
            ------------------------------------------------------------------------------
            ------------------------------------------------------------------------------
            ---------------------------
          </p>
        </div>
        <div class="questionsPart">

          <el-timeline style="width: 90%; padding-top: 3%">
            <el-timeline-item :timestamp="commentDate(item)" placement="top" v-for="item in this.userQuestionList" :key="item.id">
              <el-card>
                <div style="display: inline-block; width: 100%;">
                  <div style="display: inline-block; position: relative; left: 20px; vertical-align: top; width: 98%;">
                    <router-link :to="{path:'/questionWebby/'+ item.questionId}" style="text-decoration: none;">
                      <h4 style="margin-top: 0">{{item.questionTitle}}</h4>
                    </router-link>
                    <p>{{item.questionDescription}}</p>
                    <span style="margin-left: 10px; font-style: italic; font-size: small; color: #795da3">({{ item.followCount }}人已关注该博客)</span>
                    <router-link :to="{path:'/questionWebby/'+ item.questionId}" style="text-decoration: none;">
                      <span class="iconfont icon-pinglun" style="margin-left: 20px; white-space: pre; color: #0077aa">  {{ item.questionCommentCount }}条评论</span>
                    </router-link>
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
                           @current-change=this.getCommentPageRes
                           v-if="this.total > 0">
            </el-pagination>
          </el-timeline>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import MyHeader from '../components/myheader';
import userEveryQuestion from "../components/userEveryQuestion";
import MyQuestion from "../components/question";
import MyMessage from "../components/sendMessage";

import $ from '../libs/util';

export default {
  name: "userWebby",
  inject: ['reload'],
  components: {
    MyHeader,
    userEveryQuestion,
    MyQuestion,
    MyMessage
  },
  data(){
    return{
      user: '',
      userHeadImg: '',
      userQuestionList: [],
      followeeCount: '',
      followerCount: '',
      followed: '',
      commentCount: '',
      followButtonText: '',
      srcList: [],   //用于存放头像的大图预览,
      followUserButton: 'display: none; margin-left: 20px;',
      unFollowUserButton: 'display: inline; margin-left: 20px;',
      currentPage: 1,   //当前页码
      total: 0,   //总问题博客数量
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
        let timeDate = new Date(item.questionCreatedDate);
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
    followTheUser(){

      const _this = this;

      if(this.followButtonText === '取消关注'){
        //点击取关该用户
        $.ajax.get(
          '/unfollowUserWebby',
          {
            params: {
              userId: _this.$route.params.id
            },
            headers: {
              "Authorization": localStorage.getItem("token")
            }
          }).then(res => {
            if (res.data.code === "my200") {
              /*
              * 关注或取关，被关注用户，其数据也会变化，原计划使用_this.reload();
              * 缺点也很明显，页面闪烁，或其他初始化等异常情况，因reload会调用到mounted
              * */
              //_this.reload();
              _this.followerCount = _this.followerCount - 1;
              if (_this.user.id === _this.$store.state.user.id) {
                //关注或取关自己，关注数也要修改
                _this.followeeCount = _this.followeeCount - 1;
              }
              _this.followUserButton = 'display: inline; margin-left: 20px;';
              _this.unFollowUserButton = 'display: none; margin-left: 20px;';
              _this.followButtonText = '关注他（她）';
              _this.$message({
                message: '已成功取消关注~',
                type: 'success'
              });
            } else if (res.data.code === "my402") {
              //服务器异常，请联系管理员
              _this.$message({
                message: '服务器异常，请联系管理员',
                type: 'error'
              });
            } else {
              //token有问题，1、清空原token与原user；2、进入登陆页面；3、重定向回当前页面
              _this.$store.commit("REMOVE_TOKEN");
              _this.$store.commit("REMOVE_USER");
              _this.$router.push({path:'/loginWebby', query:{next: "/userWebby/" + _this.$route.params.id}});
            }
        }).catch(error => {
          console.log(error);
          _this.$message({
            message: '服务器异常，请联系管理员',
            type: 'error'
          });
        })
      } else {
        //点击关注该用户
        $.ajax.get(
          '/followUserWebby',
          {
            params:{
              userId: _this.$route.params.id
            },
            headers: {
              "Authorization": localStorage.getItem("token")
            }
          }).then(res => {
            if (res.data.code === "my200") {
              //同上
              _this.followerCount = _this.followerCount + 1;
              if (_this.user.id === _this.$store.state.user.id) {
                //关注或取关自己，关注数也要修改
                _this.followeeCount = _this.followeeCount + 1;
              }
              _this.followUserButton = 'display: none; margin-left: 20px;';
              _this.unFollowUserButton = 'display: inline; margin-left: 20px;';
              _this.followButtonText = '取消关注';
              _this.$message({
                message: '已成功关注~',
                type: 'success'
              });
            } else if (res.data.code === "my402") {
              //服务器异常，请联系管理员
              _this.$message({
                message: '服务器异常，请联系管理员',
                type: 'error'
              });
            } else {
              //token有问题，1、清空原token与原user；2、进入登陆页面；3、重定向回当前页面
              _this.$store.commit("REMOVE_TOKEN");
              _this.$store.commit("REMOVE_USER");
              _this.$router.push({path:'/loginWebby', query:{next: "/userWebby/" + _this.$route.params.id}});
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
    getCommentPageRes(currentPage){

      const _this = this;

      $.ajax.get(
        '/userWebby/' + _this.$route.params.id,
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

          _this.user = result.user;
          _this.userHeadImg = result.user.headUrl;
          _this.srcList.push(result.user.headUrl);  //头像预览图
          _this.userQuestionList = result.userQuestionList;   //后台直接返回的是jsonArray，不是jsonArray.toJSONString()
          _this.followeeCount = result.followeeCount;
          _this.followerCount = result.followerCount;
          _this.followed = result.followed;
          _this.commentCount = result.commentCount;
          _this.total = result.totalQuestionCount;    //todo:这里测试

          if(_this.followed){
            //当前登录用户，已关注了该用户
            _this.followUserButton = 'display: none; margin-left: 20px;';
            _this.unFollowUserButton = 'display: inline; margin-left: 20px;';
            _this.followButtonText = '取消关注';
          }else{
            //当前登录用户，未关注该用户
            _this.followUserButton = 'display: inline; margin-left: 20px;';
            _this.unFollowUserButton = 'display: none; margin-left: 20px;';
            _this.followButtonText = '关注他（她）';
          }
        } else if (res.data.code === "my402") {
          //服务器异常，请联系管理员
          _this.$message({
            message: '服务器异常，请联系管理员',
            type: 'error'
          });
        } else {
          //token有问题，1、清空原token；2、进入登陆页面；3、重定向回当前页面
          _this.$store.commit("REMOVE_TOKEN");
          _this.$store.commit("REMOVE_USER");
          _this.$router.push({path:'/loginWebby', query:{next: "/userWebby/" + _this.$route.params.id}});
        }
      }).catch(error => {
        console.log(error);
        _this.$message({
          message: '服务器异常，请联系管理员',
          type: 'error'
        });
      });
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
}
.userBorder{
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  top: 8%;
  left: 15%;
  width: 1600px;
  height: 1100px;
  padding: 20px 15px;
}
.userImg{
  position: relative;
  display: inline-block;
  width: 70px;
  height: 70px;
}

.userPart{
  /*position: relative;*/
  position: fixed;
  top: 100px;
  left: 700px;
  width: 1000px;
  height: 100px;
  border-style: solid;
  border-width: 2px;
  border-color: #DD4A68;
}

.followButton{
  display: block;
  position: relative;
  top: 20px;
  left: 20px;
  width: 80px;
  height: 33px;
  background-color: cornflowerblue;
  color: white;
  font-weight: bold;
  font-size: 14px;
  border-style: solid;
  border-width: 1px;
  border-color: #0077aa;
}
.questionsPart{
  position: relative;
  left: 10%;
  width: 79.5%;
  height: 83%;
  border-style: solid;
  border-width: 2px;
  border-color: #b3d4fc;
  overflow-y: auto;
  padding-bottom: 1%;
}
.everyQuestion{
  position: relative;
  top: 20px;
  left: 20px;
  width: 95%;
  height: 120px;
  border-style: solid;
  border-width: 2px;
  border-color: chartreuse;
  overflow-x: auto;
  margin-bottom: 20px;
  overflow-y: auto;
  background-color: rgba(204,204,204,0.2);
}
</style>
