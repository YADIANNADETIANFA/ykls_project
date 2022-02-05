<template>
  <div>
    <el-container>
      <!-- 头部菜单 -->
      <el-header style="padding: 0">
        <el-row>
          <el-col :span="6">
            <el-menu
              class="el-menu-demo"
              mode="horizontal"
              @select="handleSelectLeft"
              background-color="#545c64"
              text-color="#fff"
              active-text-color="#ffd04b"
              v-if="$route.meta.showNav">
              <el-menu-item index="/indexWebby">主站</el-menu-item>
              <el-menu-item index="/blogEditorWebby">我要发帖</el-menu-item>
              <el-menu-item index="" @click="dialog = true">发送私信</el-menu-item>
              <el-menu-item index="" @click="showElAside">菜单</el-menu-item>
              <!--              <el-menu-item index="4"><a href="https://www.ele.me" target="_blank">订单管理</a></el-menu-item>-->
            </el-menu>
          </el-col>
          <el-col :span="14">
            <el-menu
              class="el-menu-demo"
              mode="horizontal"
              background-color="#545c64"
              text-color="#fff"
              active-text-color="#ffd04b"
              v-if="$route.meta.showNav">
              <el-menu-item class = my-el-input__inner index="">
<!--                <template>
                  站内搜索：
                  <el-input
                    placeholder="请输入关键字"
                    prefix-icon="el-icon-search"
                    v-model="searchWords">    &lt;!&ndash; v-model="this.searchWords" 还会报错呢 &ndash;&gt;
                  </el-input>
                </template>-->

                <!-- v-model="this.searchWords" 还会报错呢 -->
                <el-input placeholder="请输入关键字" v-model="searchWords" class="input-with-select" @keyup.enter.native="searchES">
                  <el-select v-model="searchSelect" slot="prepend" placeholder="请选择：" style="width: 100px">
                    <el-option label="用户" value="1"></el-option>
                    <el-option label="帖子" value="2"></el-option>
                  </el-select>
                  <el-button slot="append" icon="el-icon-search" @click="searchES"></el-button>
                </el-input>

              </el-menu-item>
            </el-menu>
          </el-col>
          <el-col :span="4">
            <el-menu
              class="el-menu-demo"
              mode="horizontal"
              background-color="#545c64"
              text-color="#fff"
              active-text-color="#ffd04b"
              :router="true"
              v-if="$route.meta.showNav">
              <el-submenu index="">
                <template slot="title">
                  <el-image style="width: 40px; height: 40px" :src="this.userHeadImg" :preview-src-list="this.srcList"></el-image>
                  {{ this.userName }}
                </template>
                <el-menu-item :index="'/userWebby/' + this.userId">
                  <div>
                    <i class="el-icon-s-home"></i>
                    我的主页
                  </div>
                </el-menu-item>
                <el-menu-item :index="'/userWebby/' + this.userId + '/followers'">
                  <div>
                    <i class="el-icon-user-solid"></i>
                    我的粉丝
                  </div>
                </el-menu-item>
                <el-menu-item :index="'/userWebby/' + this.userId + '/followees'">
                  <div>
                    <i class="el-icon-star-on"></i>
                    我所关注
                  </div>
                </el-menu-item>
                <el-menu-item index="/msgWebby/list">
                  <div>
                    <i class="el-icon-message"></i>
                    我的私信
                  </div>
                </el-menu-item>
                <el-menu-item index="" @click="logout">
                  <div>
                    <i class="el-icon-error"></i>
                    退出登录
                  </div>
                </el-menu-item>
              </el-submenu>
            </el-menu>
          </el-col>
        </el-row>
      </el-header>
      <el-container id="elContainer" :style="this.elContainerStyle">
        <!-- 侧边栏菜单 -->
        <el-aside id="elAsideId" width="200px" v-if="$route.meta.showNav" :style="this.elAsideStyle">
          <el-menu :router="true" background-color="#545c64" text-color="#fff" active-text-color="#ffd04b">
            <el-submenu index="">
              <template slot="title">最新动态</template>
              <el-menu-item-group>
                <el-menu-item index="/pullfeeds"><i class="el-icon-bottom"></i>动态pull</el-menu-item>
                <el-menu-item index="/pushfeeds"><i class="el-icon-top"></i>动态push</el-menu-item>
              </el-menu-item-group>
            </el-submenu>
          </el-menu>
        </el-aside>
        <!-- 主要内容 -->
        <el-main :style="this.elMainStyle">
          <!--  router-view负责根据路由，渲染不同页面组件，渲染出来的内容与myApp.vue template中的其他内容同级  -->
          <router-view v-if="this.isRouterAlive"></router-view>
        </el-main>
      </el-container>
    </el-container>
<!--  MyMessage感觉后续可以删掉了  -->
    <MyMessage v-show="this.isShowMessageWindow" class="myMessage"></MyMessage>

    <el-drawer
      title="发送私信："
      :visible.sync="dialog"
      direction="btt"
      custom-class="demo-drawer"
      ref="drawer"
      :show-close="false">
      <div class="demo-drawer__content">
        <el-form :model="form" :rules="rules" ref="form" style="position: relative; width: 90%">
          <el-form-item label="接收人：" prop="receiver" :label-width="formLabelWidth">
            <el-input v-model="form.receiver" autocomplete="off" maxlength="50" show-word-limit></el-input>
          </el-form-item>
          <el-form-item label="私信内容：" prop="content" :label-width="formLabelWidth">
            <el-input
              type="textarea"
              :autosize="{ minRows: 5, maxRows: 5}"
              placeholder="请输入私信内容~"
              resize="none"
              v-model="form.content" maxlength="300" show-word-limit>
            </el-input>
          </el-form-item>
        </el-form>
        <div class="demo-drawer__footer" style="position: relative; text-align: right; right: 10%">
          <el-button @click="cancelForm">取 消</el-button>
          <el-button type="primary" @click="confirmForm">确 定</el-button>
        </div>
      </div>
    </el-drawer>


  </div>
</template>

<script>
import $ from './libs/util';
import $post from './libs/utilPost';
import MyQuestion from "./components/question";
import MyMessage from "./components/sendMessage";

export default {
  name: "myApp",
  components: {
    MyQuestion,
    MyMessage
  },
  created(){
    //在页面加载时读取sessionStorage里的状态信息
    if(sessionStorage.getItem('store')){
      this.$store.replaceState(Object.assign({}, this.$store.state, JSON.parse(sessionStorage.getItem('store'))))
    }

    //在页面刷新时将vuex里的信息保存到sessionStorage里
    //beforeunload事件在页面刷新时先触发
    window.addEventListener('beforeunload', () => {
      sessionStorage.setItem('store', JSON.stringify(this.$store.state))
    })
  },
  provide(){
    return {
      reload: this.reload
    }
  },
  data(){
    return {
      isRouterAlive: true,
      searchWords: '',
      elAside: true,
      isShowMessageWindow: false,
      fit: 'fill',
      srcList: [],   //用于存放头像的大图预览
      elContainerStyle: '',
      elAsideStyle: 'visibility: hidden;',
      elMainStyle: 'overflow-x: auto; overflow-y: auto;',
      dialog: false,
      form: {
        receiver: '',
        content: ''
      },
      rules: {
        receiver: [
          {required: true, message: '请输入接收人', trigger: 'blur'}
        ],
        content: [
          {required: true, message: '请输入私信内容', trigger: 'blur'}
        ]
      },
      formLabelWidth: '120px',
      searchSelect: ''
    }
  },
  methods: {
    reload() {
      this.isRouterAlive = false;
      this.$nextTick(function(){
        this.isRouterAlive = true;
      })
    },
    handleSelectLeft(key, keyPath) {
      this.$router.push(key);
    },
    logout() {
      const _this = this;
      $.ajax.get(
        'logoutWebby',
        {
          headers: {
            "Authorization": localStorage.getItem("token")
          }
        }).then((res) => {
        if(res.data.code === "my200"){
          _this.$store.commit("REMOVE_TOKEN");
          _this.$store.commit("REMOVE_USER");
          _this.$router.push({path: '/loginWebby'});
        }
      }).catch(error => {
        console.log(error);
        alert("退出登录异常，请联系管理员");
      })
    },
    showElAside() {
      this.elAside = !this.elAside;
      let obj = document.getElementById("elAsideId");
      if (this.elAside === true) {
        obj.style.visibility = 'hidden';
      } else {
        obj.style.visibility = 'visible';
      }
    },
    showMessageWindow() {
      this.isShowMessageWindow = !this.isShowMessageWindow;
    },
    confirmForm() {
      const _this = this;
      this.$refs.form.validate((valid) => {
        if (valid) {
          //表单整体校验通过
          let params = {
            toName: _this.form.receiver,
            content: _this.form.content
          }
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
              _this.$message({
                message: '私信发送成功~',
                type: 'success'
              });
              //清空内容
              this.dialog = false;
              this.form.receiver = '';
              this.form.content = '';
            } else if (res.data.code === "my402") {
              _this.$message({
                message: '服务器异常，请联系管理员',
                type: 'error'
              });
            }else if(res.data.code === "my403") {
              _this.$message({
                message: '请输入正确的接收人信息',
                type: 'info'
              });
            } else {
              //token有问题，1、清空原token；2、进入登陆页面；3、重定向回当前页面
              _this.$store.commit("REMOVE_TOKEN");
              _this.$store.commit("REMOVE_USER");
              _this.$router.push({path:'/loginWebby', query:{next: "/indexWebby"}});
              //清空内容
              this.dialog = false;
              this.form.receiver = '';
              this.form.content = '';
            }
          }).catch(error => {
            console.log(error);
            _this.$message({
              message: '服务器异常，请联系管理员',
              type: 'error'
            });
          })
        } else {
          _this.$message({
            message: '发送失败，请核对后再提交',
            type: 'error'
          })
        }
      })
    },
    cancelForm() {
      //清空内容
      this.dialog = false;
      this.form.receiver = '';
      this.form.content = '';
    },
    searchES() {
      this.$router.push({
        path:'/searchWebby', query:{searchSelect: this.searchSelect, searchWords: this.searchWords}
      });
    }
  },
  computed: {
    userId() {
      return this.$store.state.user.id;
    },
    userName() {
      return this.$store.state.user.name;
    },
    userHeadImg() {
      //头像预览图
      this.srcList.push(this.$store.state.user.headUrl);
      return this.$store.state.user.headUrl;
    }
  },
  //watch实现仅/indexWebby页面展示背景图，其他页面不展示
  watch: {
    //监视路由变化
    $route(to, from) {
      //linux and windows
      // if (window.location.href === "http://120.*.*.30/#/indexWebby") {
      if (window.location.href === "http://192.168.1.10/#/indexWebby") {
        this.elContainerStyle = "position: relative; top: 0; left: 0; width: 100%; height: 1250px; min-width: 1000px; zoom: 1; background: url(../static/images/tianjiang.jpg) no-repeat; background-size: cover; -webkit-background-size: cover; -o-background-size: cover;";
        this.elAsideStyle = "background: rgba(255,255,255,0.5); visibility: hidden";
        this.elMainStyle = "overflow-x: auto; overflow-y: auto; background: rgba(255,255,255,0.5);"
      } else {
        this.elContainerStyle = "";
        this.elAsideStyle = "visibility: hidden";
        this.elMainStyle = "overflow-x: auto; overflow-y: auto;"
      }
    }
  }
}
</script>

<style scoped>

.my-el-input__inner {
  width: 80%;
}

.input-with-select{
  background-color: #fff;
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
.backgroundImg{
  /*让整个div固定在屏幕左上方*/
  position: relative;
  top: 0;
  left: 0;
  /*跟屏幕大小一样，达到全屏效果，min-width是为了实现让屏幕宽度在1000px以内时，div大小保持不变，也就是图片不缩放*/
  width: 100%;
  height: 100%;
  min-width: 1000px;
  /*默认层级为0，越小越底层，为了成为背景图片*/     /*不要乱加，加了之后头像上传组件会被影响*/
  /*z-index: -10;*/
  zoom: 1;
  /*图片平铺，不重复*/
  background: url(../static/images/tianjiang.jpg) no-repeat;
  /*图片随屏幕大小同步缩放*/
  background-size: cover;
  -webkit-background-size: cover;
  -o-background-size: cover;
  /*图片透明度，但是设置后，连内部文字也会变得透明。。。*/
  /*opacity: 0.2;*/
}
</style>
