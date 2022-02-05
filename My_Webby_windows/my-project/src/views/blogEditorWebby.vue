<template>
  <div>
    <el-form :model="ruleForm" :rules="rules" ref="ruleForm" label-width="100px" class="demo-ruleForm">

      <el-form-item label="标题" prop="name">
        <el-input type="textarea" v-model="ruleForm.title"></el-input>
      </el-form-item>

      <el-form-item label="摘要" prop="total">
        <el-input type="textarea" v-model="ruleForm.description"></el-input>
      </el-form-item>

      <el-form-item label="内容" prop="content">
        <mavon-editor
          v-model="ruleForm.content"
          ref="md"
          @imgAdd="handleEditorImgAdd"
          @imgDel="handleEditorImgDel">
        </mavon-editor>
      </el-form-item>


      <el-form-item>
        <el-button type="primary" @click="submitForm('ruleForm')">立即创建</el-button>
        <el-button @click="resetForm('ruleForm')">重置</el-button>
      </el-form-item>

    </el-form>
  </div>
</template>

<script>
import $post from '../libs/utilPost';
import $ from '../libs/util';

export default {
  name: "blogEditorWebby",
  data() {
    return {
      ruleForm: {
        title: '',
        description: '',
        content: ''
      },
      rules: {
        title: [
          { required: true, message: '请填写标题', trigger: 'blur' }
        ],
        description: [
          { required: true, message: '请填写摘要', trigger: 'blur' }
        ],
        content: [
          { required: true, message: '请填写内容', trigger: 'blur' }
        ]
      },
      operation: 'add',    //add:新增  edit：编辑
      questionId: '-1'       //edit编辑时，当前问题博客的id
    }
  },
  methods: {
    submitForm(formName) {
      const _this = this;
      this.$refs[formName].validate((valid) => {
        if (valid) {
          $post.ajax.post(
            '/questionWebby/add',
            _this.ruleForm,
            {
              headers: {
                "Authorization": localStorage.getItem("token")
              },
              params: {                 /* get或post请求，url后面跟着的参数设置 */
                operation: this.operation,
                questionId: this.questionId
              }
            }
          ).then(res => {
            if (res.data.code === "my200") {
              _this.$message({
                message: '操作成功',
                type: 'success'
              });
              this.$refs[formName].resetFields();
              _this.$router.push({path:'/indexWebby'});
            } else {
              _this.$message({
                message: '创建失败，请联系管理员',
                type: 'error'
              });
            }
          }).catch(error => {
            console.log(error);
            _this.$message({
              message: '创建失败，请联系管理员',
              type: 'error'
            });
          });
        } else {
          _this.$message({
            message: '操作失败，请核对后再提交',
            type: 'error'
          });
        }
      });
    },
    resetForm(formName) {
      this.$refs[formName].resetFields();
    },
    handleEditorImgAdd(pos, $file) {
      let _this = this;
      //第一步，将图片上传到服务器
      let formdata = new FormData();
      //这里的'image'即对应的是后台需要接受的参数名
      formdata.append('image', $file);
      $post.ajax.post(
        '/upMdImg',
        formdata,
        {
          headers: {
            'Content-Type': 'multipart/form-data',
            "Authorization": localStorage.getItem("token")
          }
        }
      ).then(res => {
        if (res.data.code === "my200") {
          let result = JSON.parse(res.data.data);
          let url = result.mdImgUrl;
          // 第二步.将返回的url替换到文本原位置
          //图片回显到编辑器添加图片的位置
          _this.$refs.md.$imglst2Url([[pos, url]]);
        } else {
          _this.$message({
            message: '图片上传失败，请联系管理员',
            type: 'error'
          });
        }
      }).catch(error => {
        console.log(error);
        _this.$message({
          message: '图片上传失败，请联系管理员',
          type: 'error'
        });
      });
    },
    handleEditorImgDel(pos) {
      let _this = this;
      let params = {
        deleteImgUrl: pos[0]
      }
      $post.ajax.post(
        '/deleteMdImg',
        JSON.stringify(params),
        {
          headers: {
            'Content-Type': 'application/json',
            "Authorization": localStorage.getItem("token")
          }
        }
      ).then(res => {
        if (res.data.code !== "my200") {
          _this.$message({
            message: '服务器删除图片失败，请联系管理员',
            type: 'error'
          });
        }
      }).catch(error => {
        console.log(error);
        _this.$message({
          message: '服务器删除图片失败，请联系管理员',
          type: 'error'
        });
      });
    }
  },
  //为了点击“编辑”时，问题博客原内容的回显展示
  mounted() {
    let questionId = this.$route.query.questionId;
    if (questionId) {
      const _this = this;
      $.ajax.get(
        '/questionReviewWebby/' + questionId,
        {
          headers: {
            "Authorization": localStorage.getItem("token")
          }
        }
      ).then(res => {
        if (res.data.code === "my200") {
          let result = JSON.parse(res.data.data);
          _this.ruleForm.title = result.question.title;  //原问题博客标题
          _this.ruleForm.description = result.question.description;  //原问题博客摘要
          _this.ruleForm.content = result.question.content;  //原问题博客内容
          _this.operation = "edit";
          _this.questionId = questionId;
        } else if (res.data.code === "my403") {
          _this.$message({
            message: '抱歉，您无权修改他人的博客~',
            type: 'error'
          });
          _this.$router.push({path:'/questionWebby/' + questionId});
        } else {
          _this.$message({
            message: '数据获取失败，请联系管理员',
            type: 'error'
          });
        }
      }).catch(error => {
        console.log(error);
        _this.$message({
          message: '数据获取失败，请联系管理员',
          type: 'error'
        });
      })
    }
  }
}
</script>

<style scoped>

</style>
