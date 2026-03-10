const app = getApp();

Page({
  data: {
    selectedImage: '',      // 用户选择的原始图像
    transparentBase64: '',  // 透明底的 Base64 图像
    coloredBase64: '',      // 最终上色的 Base64 图像
    showColorPicker: false, // 是否展示颜色选择器
    colorPickerVisible: false, // 颜色盘是否可见
    isCustomColor: false, // 当前是否选用自定义颜色
    currentBgColor: '#FFFFFF', // 当前背景色
    colorPickerPopupProps: {
      zIndex: 11500,
      overlayProps: {
        zIndex: 11000,
        backgroundColor: 'transparent',
        style: 'background-color: transparent;'
      }
    },
    presetSizes: [          // 常用尺寸预设
      { name: '一寸照', width: 295, height: 413 },
      { name: '二寸照', width: 413, height: 579 },
      { name: '小一寸', width: 260, height: 378 },
      { name: '小二寸', width: 390, height: 567 }
    ],
    currentSizeIndex: 0,    // 当前选中的尺寸索引
    presetColors: [         // 预设背景色
      { name: '白', value: '#FFFFFF' },
      { name: '蓝', value: '#638cce' },
      { name: '红', value: '#FF0000' }
    ]
  },

  onLoad(options) {
    if (options.src) {
      this.setData({
        selectedImage: decodeURIComponent(options.src)
      });
      // 开始上传处理透明图
      this.processTransparentImage(this.data.selectedImage);
    } else {
      wx.navigateBack();
    }
  },


  // 选择尺寸
  onSizeSelect(e) {
    const newIndex = e.currentTarget.dataset.index;
    if (newIndex === this.data.currentSizeIndex) return;

    this.setData({
      currentSizeIndex: newIndex
    });
    
    // 如果已经选择了图片，修改尺寸后需要重新抠图处理
    if (this.data.selectedImage) {
      this.processTransparentImage(this.data.selectedImage);
    }
  },

  // 1. 生成透明图片
  processTransparentImage(filePath) {
    wx.showLoading({ title: '魔法正在生效...', mask: true });
    
    // 获取当期选中的尺寸参数
    const sizeObj = this.data.presetSizes[this.data.currentSizeIndex];

    wx.uploadFile({
      url: `${app.globalData.baseUrl}/api/photo/generate-idphoto`, 
      filePath: filePath,
      name: 'file',
      formData: {
        height: sizeObj.height,
        width: sizeObj.width
      },
      success: (res) => {
        try {
          const data = JSON.parse(res.data);
          if (data.code === 200) {
            // 优先使用高清图(hd)，如果没有则兜底使用标准图
            let base64Image = data.data.image_base64_hd || data.data.image_base64_standard || '';
            base64Image = base64Image.replace(/^data:image\/\w+;base64,/, '').replace(/[\r\n\s]/g, '');
            this.setData({
              transparentBase64: base64Image,
              showColorPicker: true
            });
            // 顺便生成一张当时所选背景色的预览图，默认为白色
            this.changeBackgroundColor(this.data.currentBgColor || '#FFFFFF');
          } else {
            wx.showToast({ title: '处理失败, 请重试', icon: 'none' });
          }
        } catch (e) {
          wx.showToast({ title: '系统异常，解析失败', icon: 'none' });
        }
      },
      fail: (err) => {
        wx.showToast({ title: '请求失败', icon: 'none' });
      },
      complete: () => {
        wx.hideLoading();
      }
    });
  },

  // 2. 变换背景色API调用
  changeBackgroundColor(color) {
    if (!this.data.transparentBase64) return;
    
    this.setData({ currentBgColor: color });
    wx.showLoading({ title: '魔法正在生效...', mask: true });

    wx.request({
      url: `${app.globalData.baseUrl}/api/photo/add-background`,
      method: 'POST',
      header: {
        'content-type': 'application/x-www-form-urlencoded'
      },
      data: {
        base64Image: this.data.transparentBase64,
        color: color
      },
      success: (res) => {
        if (res.data.code === 200) {
          let base64 = res.data.data.image_base64 || '';
          base64 = base64.replace(/^data:image\/\w+;base64,/, '').replace(/[\r\n\s]/g, '');
          this.setData({
            coloredBase64: base64
          });
        } else {
          wx.showToast({ title: '换底色失败', icon: 'none' });
        }
      },
      fail: () => {
        wx.showToast({ title: '请求失败', icon: 'none' });
      },
      complete: () => {
        wx.hideLoading();
      }
    });
  },

  // 点击选择颜色
  onColorSelect(e) {
    const color = e.currentTarget.dataset.color;
    this.setData({ isCustomColor: false });
    this.changeBackgroundColor(color);
  },

  // 展开色盘
  showColorPickerPopup() {
    this.setData({ colorPickerVisible: true });
  },

  // 拾取颜色关闭回调
  onColorPickerClose() {
    this.setData({ colorPickerVisible: false });
  },

  // 拾取新颜色触发
  onCustomColorChange(e) {
    const color = e.detail.value;
    this.setData({
      isCustomColor: true,
      currentBgColor: color
    });
    this.changeBackgroundColor(color);
  },

  // 放弃重选，返回上一页
  reselect() {
    wx.navigateBack();
  },

  // 保存相册及历史
  savePhoto() {
    if (!app.globalData.openid) {
      wx.showModal({
        title: '温馨提示',
        content: '请先前往“我的”进行登录',
        success: (res) => {
          if (res.confirm) {
            app.globalData.isFromEditForLogin = true; wx.switchTab({ url: '/pages/profile/profile' });
          }
        }
      });
      return;
    }

    if (!this.data.coloredBase64) return;

    wx.showLoading({ title: '魔法正在生效...', mask: true });

    // 1. 同步保存到历史记录云端
    wx.request({
      url: `${app.globalData.baseUrl}/api/history/save`,
      method: 'POST',
      data: {
        openid: app.globalData.openid,
        base64Image: this.data.coloredBase64,
        bgColor: this.data.currentBgColor
      },
      success: (res) => {
        if (res.data.code === 200) {
          // 2. 保存到相册
          this.saveToAlbum(this.data.coloredBase64);
        } else if (res.data.code === 4031) {
          // 需要看广告
          this.showAdDialog();
        } else {
          wx.showToast({ title: '历史记录保存失败', icon: 'none' });
        }
      },
      complete: () => {
        wx.hideLoading();
      }
    });
  },

  showAdDialog() {
    wx.showModal({
      title: '免费次数已用完',
      content: '今日免费保存次数已达上限(5次)，观看广告即可继续保存！',
      confirmText: '看广告',
      cancelText: '取消',
      success: (res) => {
        if (res.confirm) {
          this.playVideoAd();
        }
      }
    });
  },

  playVideoAd() {
    if (wx.createRewardedVideoAd) {
      const adUnitId = app.globalData.rewardedVideoAdUnitId || app.globalData.defaultRewardedVideoAdUnitId || '';
      if (!adUnitId) {
        wx.showToast({ title: '广告配置缺失，请稍后再试', icon: 'none' });
        return;
      }

      if (!this.videoAd) {
        this.videoAd = wx.createRewardedVideoAd({
          adUnitId: adUnitId
        });
      } else if (this.videoAdUnitId !== adUnitId) {
        // 后端配置更新后，重建实例确保使用最新广告位
        this.videoAd = wx.createRewardedVideoAd({
          adUnitId: adUnitId
        });
      }
      this.videoAdUnitId = adUnitId;

      const closeHandler = (res) => {
        if (res && res.isEnded) {
          // 观看完毕，调后端接口记录
          this.recordAdViewAndSave();
        } else {
          wx.showToast({ title: '需要观看完整广告哦', icon: 'none' });
        }
        this.videoAd.offClose(closeHandler);
      };

      this.videoAd.onClose(closeHandler);

      this.videoAd.show().catch(() => {
        this.videoAd.load()
          .then(() => this.videoAd.show())
          .catch(err => {
            wx.showToast({ title: '广告加载失败', icon: 'none' });
            this.videoAd.offClose(closeHandler);
          });
      });
    } else {
      wx.showToast({ title: '当前环境不支持视频广告', icon: 'none' });
    }
  },

  recordAdViewAndSave() {
    wx.showLoading({ title: '魔法正在生效...', mask: true });
    wx.request({
      url: `${app.globalData.baseUrl}/api/user/recordAd`,
      method: 'POST',
      header: {
        'content-type': 'application/x-www-form-urlencoded'
      },
      data: {
        openid: app.globalData.openid
      },
      success: (res) => {
        if (res.data.code === 200) {
          wx.showToast({ title: '奖励已到账', icon: 'success' });
          // 重新发起保存
          this.savePhoto();
        } else {
          wx.showToast({ title: res.data.message || '记录异常', icon: 'none' });
        }
      },
      complete: () => {
        wx.hideLoading();
      }
    });
  },

  // 保存冲印排版照
  savePrintLayout() {
    if (!app.globalData.openid) {
      wx.showModal({
        title: '温馨提示',
        content: '请先前往“我的”进行登录',
        success: (res) => {
          if (res.confirm) {
            app.globalData.isFromEditForLogin = true; wx.switchTab({ url: '/pages/profile/profile' });
          }
        }
      });
      return;
    }

    if (!this.data.coloredBase64) return;

    wx.showLoading({ title: '魔法正在生效...', mask: true });

    const sizeObj = this.data.presetSizes[this.data.currentSizeIndex];

    wx.request({
      url: `${app.globalData.baseUrl}/api/photo/generate-layout`,
      method: 'POST',
      header: {
        'content-type': 'application/x-www-form-urlencoded'
      },
      data: {
        openid: app.globalData.openid,
        base64Image: this.data.coloredBase64,
        height: sizeObj.height,
        width: sizeObj.width
      },
      success: (res) => {
        if (res.data.code === 200) {
          let base64 = res.data.data.image_base64 || '';
          base64 = base64.replace(/^data:image\/\w+;base64,/, '').replace(/[\r\n\s]/g, '');
          
          this.saveToAlbum(base64);
        } else if (res.data.code === 4031) {
          this.showAdDialog();
        } else {
          wx.showToast({ title: '排版生成失败, 请重试', icon: 'none' });
        }
      },
      fail: () => {
        wx.showToast({ title: '排版请求失败', icon: 'none' });
      },
      complete: () => {
        wx.hideLoading();
      }
    });
  },

  saveToAlbum(base64Data) {
    const fs = wx.getFileSystemManager();
    const filePath = `${wx.env.USER_DATA_PATH}/photo_bg_temp.png`;
    const buffer = wx.base64ToArrayBuffer(base64Data);

    fs.writeFile({
      filePath,
      data: buffer,
      encoding: 'binary',
      success: () => {
        wx.saveImageToPhotosAlbum({
          filePath,
          success: () => {
            wx.showToast({ title: '保存相册成功', icon: 'success' });
          },
          fail: (err) => {
            if (err.errMsg.indexOf('auth deny') > -1) {
              wx.showToast({ title: '请授权相册权限', icon: 'none' });
            } else {
              wx.showToast({ title: data.message || '处理失败, 请重试', icon: 'none' });
            }
          }
        });
      },
      fail: () => {
        wx.showToast({ title: '文件写入失败', icon: 'none' });
      }
    });
  }
});