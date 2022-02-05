<template>
  <div>
    <div style="height: 100%; position: relative; left: 10%">
      <div class="questionBorder">
        <div style="padding-left: 5%">
          <router-link class="imgRouter" :to="'/userWebby/' + this.questionOwner.id">
            <img id="myImg1" :src="this.questionOwner.headUrl" alt="用户头像">
          </router-link>
          <h3 style="display: inline-block; padding-left: 2%; margin-top: 40px; vertical-align: top;">{{this.questionOwner.name}}</h3>
          <h2 style="white-space: pre"> Title:    {{this.questionTitle}} </h2>
          <h4 style="white-space: pre"> Description:    {{this.questionDescription}} </h4>
          <el-link icon="el-icon-edit" v-if="this.ownBlog" :underline="false">
            <router-link :to="{path: '/blogEditorWebby', query: {questionId: this.$route.params.id}}" >编辑</router-link>
          </el-link>
          <el-button style="margin-left: 40px" type="text" icon="el-icon-delete" v-if="this.ownBlog" @click="this.deleteQuestion">删除</el-button>
          <el-button type="primary" @click="followThisQuestion" :style="this.followQuestionButton" plain>{{ this.followQuestionText }}</el-button>
          <el-button type="info" @click="followThisQuestion" :style="this.unFollowQuestionButton" plain>{{ this.followQuestionText }}</el-button>
          <div style="position: relative; display: inline; margin-left: 10px; top: 6px;">
            <span style="font-size: 14px;">{{ this.followUsersCount }}</span>
            <span style="font-size: 14px;">人关注了该问题</span>
          </div>
        </div>
        <el-divider></el-divider>
        <div style="overflow-y: scroll; max-height: 75%">
          <div class="markdown-body" v-html="this.questionContent"></div>
        </div>
      </div>
      <div class="block" style="box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1); top: 97%; left: 15%; width: 1800px; height: 1100px; padding: 100px 15px; overflow-x: auto; overflow-y: scroll">
        <el-timeline>
          <el-timeline-item :timestamp="commentDate(item)" placement="top" v-for="item in this.getCommentList" :key="item.id">
            <el-card>
              <div style="display: inline-block; width: 1500px;">
                <router-link class="imgRouter" :to="'/userWebby/' + item.user.id">
                  <img id="myImg2" :src="item.user.headUrl" alt="用户头像">
                </router-link>
                <div style="display: inline-block; position: relative; left: 20px; vertical-align: top; width: 1400px;">
                  <h4>{{item.user.name}}</h4>
                  <p>{{item.comment.content}}</p>
                </div>
              </div>
              <div style="display: inline-block; position: relative; bottom: 10px; left: 20px;">
                <el-button type="success" icon="el-icon-caret-top" size="mini" @click="commentUp(item)" plain>{{ item.likeCount }}</el-button>
                <el-button type="info" icon="el-icon-caret-bottom" size="mini" @click="commentDown(item)" plain></el-button>
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
      <div class="block" style="box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1); top: 97%; left: 15%; width: 1800px; height: 250px; padding: 100px 15px; background-color: #e3e3ea; margin-top: 30px;">
        <el-input
          type="textarea"
          :autosize="{ minRows: 10, maxRows: 10}"
          placeholder="请输入您的评论"
          resize="none"
          v-model="commentText" maxlength="2000" show-word-limit>
        </el-input>
        <el-button id="commentSubmit" type="info" @click="addComment" plain>  提交  </el-button>
      </div>
    </div>
  </div>
</template>

<script>
import MyHeader from "../components/myheader";
import MyComment from "../components/comment";
import MyQuestion from "../components/question";
import MyMessage from "../components/sendMessage";

import 'github-markdown-css'
import $ from '../libs/util';
import $post from '../libs/utilPost';

export default {
  name: "questionWebby",
  inject: ['reload'],
  components: {
    MyHeader,
    MyComment,
    MyQuestion,
    MyMessage
  },
  data(){
    return{
      questionTitle: '',
      questionDescription: '',
      questionContent: '',
      questionOwner: '',
      getCommentList: [],
      followUsersCount: '',
      commentText: '',
      followQuestionText: '',
      ownBlog: false,
      followQuestionButton: 'display: none; margin-left: 20px;',
      unFollowQuestionButton: 'display: inline; margin-left: 20px;',
      currentPage: 1,   //当前页码
      total: 0,   //总评论数量
      pageSize: 10    //每页大小
    }
  },
  methods: {
    followThisQuestion(){

      const _this = this;

      if(this.followQuestionText === '取消关注'){
        //点击取消关注该问题
        $.ajax.get(
          '/unfollowQuestionWebby',
          {
            params: {
              questionId: _this.$route.params.id
            },
            headers: {
              "Authorization": localStorage.getItem("token")
            }
          }).then(res => {
            if (res.data.code === "my200") {
              _this.followQuestionButton = 'display: inline; margin-left: 20px;';
              _this.unFollowQuestionButton = 'display: none; margin-left: 20px;';
              _this.followQuestionText = '关注问题';
              _this.followUsersCount -= 1;
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
              _this.$router.push({path:'/loginWebby', query:{next: "/questionWebby/" + _this.$route.params.id}});
            }
        }).catch(error => {
          console.log(error);
          _this.$message({
            message: '服务器异常，请联系管理员',
            type: 'error'
          });
        })
      } else {
        //点击关注该问题
        $.ajax.get(
          '/followQuestionWebby',
          {
            params:{
              questionId: _this.$route.params.id
            },
            headers: {
              "Authorization": localStorage.getItem("token")
            }
          }).then(res => {
            if (res.data.code === "my200") {
              _this.followQuestionButton = 'display: none; margin-left: 20px;';
              _this.unFollowQuestionButton = 'display: inline; margin-left: 20px;';
              _this.followQuestionText = '取消关注';
              _this.followUsersCount += 1;
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
              _this.$router.push({path:'/loginWebby', query:{next: "/questionWebby/" + _this.$route.params.id}});
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
    addComment(){

      if(this.commentText === ''){
        _this.$message({
          message: '请输入评论内容',
          type: 'info'
        });
        return;
      }

      let params = {
        questionId: this.$route.params.id,
        content: this.commentText
      }

      const _this = this;

      $post.ajax.post(
        '/addCommentWebby',
        JSON.stringify(params),
        {
          headers:{
            'Content-Type': 'application/json',
            "Authorization": localStorage.getItem("token")
          }
        }).then(res => {
        if (res.data.code === "my200") {
          _this.commentText = '';  //清空评论框中的内容
          _this.reload();
          _this.$message({
            message: '提交成功~',
            type: 'success'
          });
        } else if (res.data.code === "my402") {
          //服务器异常，请联系管理员
          _this.$message({
            message: '提交失败，请联系管理员',
            type: 'error'
          });
        } else {
          //token有问题，1、清空原token；2、进入登陆页面；3、重定向回当前页面
          _this.$store.commit("REMOVE_TOKEN");
          _this.$store.commit("REMOVE_USER");
          _this.$router.push({path:'/loginWebby', query:{next: "/questionWebby/" + _this.$route.params.id}});
        }
      }).catch(error => {
        console.log(error);
        _this.$message({
          message: '提交失败，请联系管理员',
          type: 'error'
        });
      })
    },
    getCommentPageRes(currentPage) {
      const _this = this;
      $.ajax.get(
        '/questionWebby/' + _this.$route.params.id,
        {
          params: {
            currentPage: currentPage
          },
          headers: {
            "Authorization": localStorage.getItem("token")
          }
        }
      ).then(res => {
        if(res.data.code === "my200"){
          let result = JSON.parse(res.data.data);
          _this.questionTitle = result.question.title;  //问题博客标题
          _this.questionDescription = result.question.description;  //问题博客摘要
          let content = result.question.content;  //问题博客内容
          _this.questionOwner = result.questionOwner; //问题博客持有者
          _this.followUsersCount = result.followUsers.length;
          _this.getCommentList = result.comments;   //问题博客该页下的具体评论
          _this.total = result.totalCommentCount;   //问题博客的总评论数

          //将md的content进行渲染
          let MarkdownIt = require("markdown-it");
          let md = new MarkdownIt();
          _this.questionContent = md.render(content);

          //当前登陆用户是否为问题博客的持有者
          _this.ownBlog = (result.questionOwner.id === _this.$store.state.user.id);

          //当前登录用户是否已关注该问题博客
          if(result.followed){
            _this.followQuestionButton = 'display: none; margin-left: 20px;';
            _this.unFollowQuestionButton = 'display: inline; margin-left: 20px;';
            _this.followQuestionText = '取消关注';
          }else{
            _this.followQuestionButton = 'display: inline; margin-left: 20px;';
            _this.unFollowQuestionButton = 'display: none; margin-left: 20px;';
            _this.followQuestionText = '关注问题';
          }

        } else if (res.data.code === "my402") {
          //服务器异常，请联系管理员
          _this.$message({
            message: '评论数据获取异常，请联系管理员',
            type: 'error'
          });
        } else{
          //token有问题，1、清空原token；2、进入登陆页面；3、重定向回当前页面
          _this.$store.commit("REMOVE_TOKEN");
          _this.$store.commit("REMOVE_USER");
          _this.$router.push({path:'/loginWebby', query:{next: "/questionWebby/" + _this.$route.params.id}});
        }
      }).catch(error => {
        console.log(error);
        //服务器异常，请联系管理员
        _this.$message({
          message: '评论数据获取异常，请联系管理员',
          type: 'error'
        });
      });
    },
    commentUp(item){

      const _this = this;

      //点赞
      $.ajax.get(
        '/likeWebby',
        {
          params: {
            commentId: item.comment.id
          },
          headers:{
            "Authorization": localStorage.getItem("token")
          }
        }).then(res => {
        if (res.data.code === "my200") {
          _this.$message({
            message: '点赞成功~',
            type: 'success'
          });
          _this.reload();
        } else if (res.data.code === "my402") {
          //已经赞过，或服务器异常，请联系管理员
          _this.$message({
            message: res.data.msg,
            type: 'warning'
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
    },
    commentDown(item){

      const _this = this;

      //点踩
      $.ajax.get(
        '/dislikeWebby',
        {
          params: {
            commentId: item.comment.id
          },
          headers: {
            "Authorization": localStorage.getItem("token")
          }
        }).then(res => {
        if (res.data.code === "my200") {
          _this.$message({
            message: '点踩成功~',
            type: 'success'
          });
          _this.reload();
        } else if (res.data.code === "my402") {
          //已经踩过，或服务器异常，请联系管理员
          _this.$message({
            message: res.data.msg,
            type: 'warning'
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
    },
    deleteQuestion() {
      const _this = this;
      this.$confirm('此操作将删除该博客, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        $.ajax.get(
          '/questionWebby/delete',
          {
            params: {
              questionId: _this.$route.params.id
            },
            headers: {
              "Authorization": localStorage.getItem("token")
            }
          }).then(res => {
            if (res.data.code === "my200") {
              this.$message({
                type: 'success',
                message: '删除成功~'
              });
              _this.$router.push({path:'/indexWebby'});
            } else if (res.data.code === "my403") {
              _this.$message({
                message: '抱歉，您无权删除他人的博客~',
                type: 'error'
              });
            } else {
              _this.$message({
                message: '服务器异常，请联系管理员',
                type: 'error'
              });
            }
        }).catch(error => {
          console.log(error);
          _this.$message({
            message: '服务器异常，请联系管理员',
            type: 'error'
          });
        })
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '您已取消删除~'
        });
      });
    }
  },
  computed: {
    //computed无法直接传参，所以使用闭包函数？
    commentDate() {
      return function(item) {
        let timeDate = new Date(item.comment.createdDate);
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
  mounted() {
    this.getCommentPageRes(1);
  }

}
</script>

<style scoped>
.mpage{
  text-align: right;
}
.questionBorder{
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  top: 8%;
  left: 15%;
  width: 1800px;
  height: 1100px;
  padding: 20px 15px;
}

.imgRouter{
  position: relative;
  display: inline-block;
  width: 60px;
  height: 60px;
  top: 20px;
}
img#myImg1{
  position: relative;
  width: 60px;
  height: 60px;
  border-style: solid;
  border-width: 1px;
  border-color: darkgray;
  display: inline;
}
img#myImg2{
  position: relative;
  width: 60px;
  height: 60px;
  border-style: solid;
  border-width: 1px;
  border-color: darkgray;
  display: inline;
}
button#commentSubmit{
  position: relative;
  top: 15%;
  left: 90%;
  width: 100px;
  height: 40px;
  padding-left: 2px;
  padding-right: 5px;
  padding-top: 10px;
  white-space:pre
}

.questionPart{
  position: relative;
  top: 60px;
  left: 640px;
  width: 1000px;
  height: 200px;
  border-style: solid;
  border-width: 2px;
  border-color: #795da3;
}
.commentsPart{
  position: relative;
  top: 90px;
  left: 640px;
  width: 1000px;
  height: 700px;
  border-style: solid;
  border-width: 2px;
  border-color: gold;
  overflow-y: auto;
  overflow-x: auto;
}
textarea#commentText{
  position: relative;
  top: 120px;
  left: 640px;
  display: block;
  border-style: solid;
  border-color: #42b983;
  border-width: 2px;
  min-width: 1000px;
  max-width: 1000px;
  min-height: 150px;
  max-height: 150px;
  font-size: 16px;
}
button#addComment{
  display: block;
  position: relative;
  top: 135px;
  left: 1550px;
  width: 80px;
  height: 33px;
  background-color: #42b983;
  color: white;
  font-size: 14px;
  border-style: solid;
  border-width: 1px;
  border-color: #63a35c;
}
.oneComment{
  position: relative;
  top: 20px;
  left: 20px;
  width: 95%;
  height: 120px;
  border-style: solid;
  border-width: 2px;
  border-color: aquamarine;
  overflow-x: auto;
  margin-bottom: 20px;
  overflow-y: auto;
  background-color: rgba(204,204,204,0.4);
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
</style>
