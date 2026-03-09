const app = getApp();

Page({
  data: {
    selectedImage: ''
  },

  onShow() {
    if (typeof this.getTabBar === 'function' && this.getTabBar()) {
      this.getTabBar().init();
    }
  },

  // 选择照片
  async chooseImage() {
    try {
      const res = await wx.chooseMedia({
        count: 1,
        mediaType: ['image'],
        sourceType: ['album', 'camera'],
        sizeType: ['original'] // 强制使用原图，保证处理质量
      });
      
      const tempFilePath = res.tempFiles[0].tempFilePath;
      // 跳转到专属编辑页面
      wx.navigateTo({
        url: `/pages/edit/edit?src=${encodeURIComponent(tempFilePath)}`
      });
    } catch (err) {
      console.log('取消选择或选择失败', err);
    }
  }
});
