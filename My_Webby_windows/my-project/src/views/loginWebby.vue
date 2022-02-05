<template>
  <div>

    <div class="zhezhao" id="zhezhao" ref="zhezhao"></div>

    <div class="backgroundImg">
      <p class="span-header">伊卡洛斯登陆系统</p>

      <div class="loginMain">
        <label for="userName" class="label-userName" ref="userNameInput">主人的账号：
          <el-input id="userName" placeholder="your userName~" size="medium" style="width: 60%"
                    prefix-icon="el-icon-user-solid" v-model="userName" maxlength="20" show-word-limit clearable
                    @blur="userNameBlurMethod" @focus="userNameFocusMethod"></el-input>
        </label>
        <label for="passWord" class="label-passWord" ref="passWordInput">主人的密码：
          <el-input id="passWord" placeholder="your passWord~" size="medium" style="width: 60%"
                    prefix-icon="el-icon-key" v-model="password" maxlength="20" show-word-limit show-password
                    @blur="passWordBlurMethod" @focus="passWordFocusMethod"></el-input>
        </label>
        <el-button id="signUp" type="info" @click="registerOrder" plain>  注册  </el-button>
        <el-button id="signIn" type="info" @click="loginOrder" plain>  登录  </el-button>
        <label for="rememberMe" class="label-rememberMe">rememberMe：
          <input id="rememberMe" type="checkbox" name="rememberMe" v-model="rememberMe">
        </label>
      </div>
    </div>

    <div v-show="registerShow" style="position: absolute; z-index: 3; width: 97.5%; height: 70%; min-height: 1000px">

      <transition name="el-zoom-in-bottom">
        <div class="transition-box">
          <el-form ref="form" :model="form" :rules="rules" label-width="80px">
            <el-form-item label="昵称" prop="name">
              <el-input v-model="form.name" style="width: 50%" prefix-icon="el-icon-user-solid" maxlength="20" show-word-limit clearable></el-input>
            </el-form-item>

            <el-form-item label="密码" prop="password">
              <el-input v-model="form.password" style="width: 50%" prefix-icon="el-icon-key" maxlength="20" show-word-limit show-password></el-input>
            </el-form-item>

            <el-form-item label="qq" prop="qq">
              <el-input v-model="form.qq" style="width: 50%"></el-input>
            </el-form-item>

            <el-form-item label="性别" prop="sex">
              <el-radio-group v-model="form.sex">
                <el-radio label="男♂"></el-radio>
                <el-radio label="女♀"></el-radio>
              </el-radio-group>
            </el-form-item>

            <el-form-item label="生日" prop="birth">
              <!--24栅格，占12份-->
              <el-col :span="12">
                <el-date-picker type="date" placeholder="选择日期" v-model="form.birth" style="width: 100%;" :editable="false"></el-date-picker>
              </el-col>
            </el-form-item>

            <el-form-item label="可能喜欢" prop="type">
              <el-checkbox-group v-model="form.type">
                <el-checkbox label="动漫" name="type"></el-checkbox>
                <el-checkbox label="LOL" name="type"></el-checkbox>
                <el-checkbox label="DNF" name="type"></el-checkbox>
                <el-checkbox label="其他" name="type"></el-checkbox>
              </el-checkbox-group>
            </el-form-item>

            <el-form-item label="签名" prop="signed">
              <el-input v-model="form.signed" style="width: 50%" prefix-icon="el-icon-edit" maxlength="80" show-word-limit></el-input>
            </el-form-item>

            <!-- linux and windows    action="http://127.0.0.1:8082/upHeadImg" -->
            <!-- aliyun    action="http://120.77.218.30:8082/upHeadImg" -->
            <el-form-item label="头像">
              <el-upload
                class="avatar-uploader"
                action="http://127.0.0.1:8082/upHeadImg"
                :show-file-list="false"
                :on-success="handleAvatarSuccess"
                :before-upload="beforeAvatarUpload">
                <img v-if="this.imageUrl" :src="this.imageUrl" class="avatar">
                <i v-else class="el-icon-plus avatar-uploader-icon"></i>
              </el-upload>
            </el-form-item>

            <el-form-item>
              <el-button :plain="true" type="primary" @click="registerPost">立即创建</el-button>
              <el-button @click="registerCancel">取消</el-button>
            </el-form-item>

          </el-form>

        </div>
      </transition>
    </div>
  </div>
</template>

<script>
import $post from '../libs/utilPost';

export default {
  name: "loginWebby",
  data(){
    let validatePassword = (rule, value, callback) => {
      if (value === '') {
        callback(new Error('请输入密码'));
      } else {
        callback();
      }
    }
    return{
      userName: '',
      password: '',
      rememberMe: '',
      imageUrl: '',   //本地图片的url，进行预览使用
      imageDataBaseUrl: '',  //数据库存储的图片url
      registerShow: false,
      form: {
        name: '',
        password: '',
        qq: '',
        sex: '',
        birth: '',
        type: [],
        signed: ''
      },
      rules: {
        name: [
          {required: true, message: '请输入昵称', trigger: 'blur'},
          {min: 5, max: 20, message: '长度在5~20个字符', trigger: 'blur'}
        ],
        password: [
          {required: true, validator: validatePassword, trigger: 'blur'},
          {min: 10, max: 20, message: '长度在10~20个字符', trigger: 'blur'}
        ],
        qq: [
          {required: true, message: '请输入qq号码', trigger: 'blur'}
        ],
        sex: [
          {required: true, message: '请输入性别', trigger: 'change'}
        ],
        birth: [
          {type: 'date', required: true, message: '请选择您的生日', trigger: 'change'}
        ],
        type: [
          {type: 'array', required: true, message: '请选择您感兴趣的内容', trigger: 'change'}
        ],
        signed: [
          {required: true, message: '请输入您的签名信息', trigger: 'blur'},
          {min: 1, max: 80, message: '长度在1~80个字符', trigger: 'blur'}
        ]
      }
    }
  },
  methods:{
    registerOrder(){
      this.registerCancel();
      this.registerShow = true;
      this.$refs.zhezhao.style.display = 'block';
    },
    registerPost() {
      const _this = this;
      this.$refs.form.validate((valid) => {
        if (valid) {
          //表单整体校验通过
          let params = {
            userName: _this.form.name,
            password: _this.form.password,
            qq: _this.form.qq,
            sex: _this.form.sex === "男♂" ? "F" : "M",  //F:男，  M：女
            birth: _this.form.birth,
            type: _this.form.type,
            signed: _this.form.signed,
            headImgUrl: _this.imageDataBaseUrl
          }
          $post.ajax.post(
            '/registerWebby',
            JSON.stringify(params),
            {
              headers: {
                'Content-Type': 'application/json'
              }
            }
          ).then(res => {
            if (res.data.code === "my200") {
              //注册时输入的内容清空
              _this.registerCancel();
              _this.$message({
                message: '注册成功~',
                type: 'success'
              });
            } else if (res.data.code === "my402") {
              _this.$message({
                message: '服务器异常，请联系管理员',
                type: 'info'
              });
            } else if (res.data.code === "my400") {
              _this.$message({
                message: res.data.msg,
                type: 'error'
              })
            }
          }).catch(error => {
            console.log(error);
            _this.$message({
              message: '服务器异常，请联系管理员',
              type: 'info'
            });
          });
        } else {
          _this.$message({
            message: '注册失败，请核对后再提交',
            type: 'error'
          })
        }
      })
    },
    loginOrder(){
      if(this.userName === '' || this.password === ''){
        // this.returnMsg = 'userName and password please~';
        this.$message({
          message: 'userName and password please~',
          type: 'error'
        });
        return;
      }
     let params = {
        userName: this.userName,
        password: this.password
      }

      const _this = this;

      $post.ajax.post(
        '/loginWebby',
        JSON.stringify(params),     /* post请求的body请求体内参数设置 */
        {
          headers:{
            'Content-Type': 'application/json'
          },
          params: {                 /* get或post请求，url后面跟着的参数设置 */
            rememberMe: this.rememberMe,
          }
        }
      )
      .then(res => {
        if(res !== null){/* 前台json的处理方式 */

          //前台查找cookie的方法，可以在webstorm的终端看，也可以直接到chrome的console下面看
          //console.log(document.cookie);
          //console.log(this.$cookies.get("ticket"));
          //console.log(this.$cookies.keys());

          _this.returnCode = res.data.code;
          if (_this.returnCode === "my200") {
            //TODO: 后续这里可做个点击按钮进入之类的，不然显示了谁看啊
            //这里注意，是路由到前台的indexWebby.vue，不是直接向后台发起indexWebby请求！
            //登陆成功，将token放入客户端存储
            let resultIn = JSON.parse(res.data.data);
            let jwt = resultIn.token;
            let user = resultIn.user;
            _this.$store.commit("SET_TOKEN", jwt);
            _this.$store.commit('SET_USER', user);

            //判断，是否登录后走重定向页面
            if(typeof(_this.$route.query.next) === "undefined"){
              _this.$router.push({path:'/indexWebby'});
            }else{
              _this.$router.push({path: _this.$route.query.next});
            }
          } else {
            _this.$message({
              message: 'login process error...',
              type: 'error'
            })
          }
        } else {
          _this.$message({
            message: 'login process error...',
            type: 'error'
          })
        }
      })
      .catch(error => {
        console.log(error);
        // _this.returnMsg = 'login process error...';
        _this.$message({
          message: 'login process error...',
          type: 'error'
        });
      })
    },

    handleAvatarSuccess(res, file) {
      //file是本地点击上传图片时，本地的图片，并不是传到服务器上之后，服务器上的图片
      //URL.createObjectURL(file.raw)生成的url，一旦缓存被清空，将无法获取该url所对应的blob对象
      this.imageUrl = URL.createObjectURL(file.raw);
      this.imageDataBaseUrl = JSON.parse(res.data).headImgUrl;
      this.$message({
        message: '头像上传成功~',
        type: 'success'
      });
    },
    beforeAvatarUpload(file) {
      let isAcceptable = false;
      switch (file.type) {
        case "image/png" :
          isAcceptable = true;
          break;
        case "image/jpeg" :
          isAcceptable = true;
          break;
        case "image/jpg" :
          isAcceptable = true;
          break;
        default :
          isAcceptable = false;
          break;
      }
      let isLt5M = file.size / 1024 / 1024 < 5;
      if (!isAcceptable) {
        this.$message.error('上传头像图片只能是 PNG、JPG、JPEG 格式');
      }
      if (!isLt5M) {
        this.$message.error('上传头像图片大小不能超过 5MB!');
      }
      return isAcceptable && isLt5M;
    },
    userNameBlurMethod() {
      this.$refs.userNameInput.style.opacity = 0.3;
    },
    passWordBlurMethod() {
      this.$refs.passWordInput.style.opacity = 0.3;
    },
    userNameFocusMethod() {
      this.$refs.userNameInput.style.opacity = 1;
    },
    passWordFocusMethod() {
      this.$refs.passWordInput.style.opacity = 1;
    },
    registerCancel() {
      this.registerShow = false;
      this.$refs.zhezhao.style.display = 'none';
      this.form.name = '';
      this.form.password = '';
      this.form.qq = '';
      this.form.sex = '';
      this.form.birth = '';
      this.form.type = [];
      this.form.signed = '';
      this.imageUrl = '';
    }
  }
}
</script>

<style scoped>

.zhezhao {
  width: 100%;
  height: 100%;
  background-color: #000;
  opacity: 0.5;
  position: absolute;
  left: 0;
  top: 0;
  display: none;
  z-index: 2;
}

.avatar-uploader .el-upload {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
}
.avatar-uploader .el-upload:hover {
  border-color: #409EFF;
}
.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 178px;
  height: 178px;
  line-height: 178px;
  text-align: center;
}
.avatar {
  width: 178px;
  height: 178px;
  display: block;
}

.transition-box {
  margin-bottom: 10px;
  width: 100%;
  height: 80%;
  border-radius: 4px;
  border-color: #795da3;
  border-width: thin;
  background-color: white;
  color: #fff;
  padding: 40px 20px;
  box-sizing: border-box;
  margin-right: 20px;
  /*设置最小高度，避免缩小窗口时，弹窗展示有问题*/
  min-height: 800px;
}


.backgroundImg{
  /*让整个div固定在屏幕左上方*/
  position: fixed;
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
  background: url(../../static/images/diantu.jpg) no-repeat;
  /*图片随屏幕大小同步缩放*/
  background-size: cover;
  -webkit-background-size: cover;
  -o-background-size: cover;
  /*图片透明度，但是设置后，连内部文字也会变得透明。。。*/
  /*opacity: 0.5;*/
}
.label-userName{
  position: relative;
  top: 10%;
  left: 5%;
  font-size: 20px;
  opacity: 0.3;
}
.label-passWord{
  display: block;
  position: relative;
  top: 20%;
  left: 5%;
  font-size: 20px;
  opacity: 0.3;
}
#userName{
  width: 220px;
  height: 25px;
}
#passWord{
  width: 220px;
  height: 25px;
}
button#signUp{
  position: relative;
  top: 30%;
  left: 25%;
  width: 100px;
  height: 40px;
  padding-top: 10px;
  padding-left: 2px;
  padding-right: 5px;
/*  background-color: darkgray;
  color: black;
  border-radius: 6px;*/
/*  font-style: italic;
  font-weight: bold;*/
  background-color: rgba(0,0,0,0.3);
  white-space:pre
}
button#signIn{
  position: relative;
  top: 30%;
  left: 40%;
  width: 100px;
  height: 40px;
  padding-left: 2px;
  padding-right: 5px;
  padding-top: 10px;
/*  background-color: darkgray;
  color: black;
  border-radius: 6px;*/
/*  font-style: italic;
  font-weight: bold;*/
  background-color: rgba(0,0,0,0.3);
  white-space:pre
 }
.span-header{
  height: 30px;
  background: rgba(0,0,0,.8);
  color: #fff;
  width: 240px;
  padding-left: 40px;
  padding-top: 9px;
  font-style: italic;
}
.loginMain{
  position: relative;
  top: 60%;
  left: 40%;
  border-color: hotpink;
  border-style: solid;
  border-width: 1px;
  width: 500px;
  height: 200px;
  z-index: -1;
}
.label-rememberMe{
  position: relative;
  top: 50%;
  left: 28%;
  font-size: 14px;
  color: white;
  font-style: italic;
}

</style>
