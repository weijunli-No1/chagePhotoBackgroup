const app = getApp();

Page({
  data: {
    userInfo: null,
    hasLogin: false
  },

  onShow() {
    if (typeof this.getTabBar === 'function' && this.getTabBar()) {
      this.getTabBar().init();
    }

    if (app.globalData.openid) {
      this.setData({ hasLogin: true, userInfo: { openid: app.globalData.openid } }); // 实际应用中这里可以拉取更多用户信息
    }
  },

  onLogin() {
    wx.showLoading({ title: '登录中...', mask: true });
    app.doLogin()
      .then(res => {
        this.setData({
          hasLogin: true,
          userInfo: res
        });
        wx.hideLoading();
        if (app.globalData.isFromEditForLogin) {
          app.globalData.isFromEditForLogin = false; // 重置标识
          wx.showModal({
            title: '登录成功',
            content: '是否立即返回去制作证件照？',
            confirmText: '去制作',
            cancelText: '留在本页',
            success: (mRes) => {
              if (mRes.confirm) {
                wx.switchTab({ url: '/pages/index/index' });
              }
            }
          });
        } else {
          wx.showToast({ title: '登录成功', icon: 'success' });
        }
      })
      .catch(err => {
        wx.hideLoading();
        // wx.showToast({ title: '登录失败', icon: 'none' });
      });
  },

  clearHistory() {
    wx.showModal({
      title: '清空缓存',
      content: '确定要清空本地缓存记录吗？将删除您所保存的一些本地临时数据。',
      confirmColor: '#e11d48',
      success: (res) => {
        if (res.confirm) {
          // 保存openid避免被误清空
          const openid = wx.getStorageSync('openid');
          wx.clearStorageSync();
          if (openid) {
            wx.setStorageSync('openid', openid);
          }
          wx.showToast({ title: '清理完成', icon: 'success' });
        }
      }
    });
  },

  onLogout() {
    wx.showModal({
      title: '退出登录',
      content: '确定要退出登录吗？',
      confirmColor: '#e11d48',
      success: (res) => {
        if (res.confirm) {
          app.globalData.openid = '';
          app.globalData.userInfo = null;
          wx.removeStorageSync('openid');
          this.setData({ 
            hasLogin: false,
            userInfo: null 
          });                                                                           
          wx.showToast({ title: '已退出登录', icon: 'none' });
        }
      }
    });
  },

  navToPrivacy() {
    wx.navigateTo({
      url: '/pages/document/document?type=privacy'
    });
  },

  navToAgreement() {
    wx.navigateTo({
      url: '/pages/document/document?type=agreement'
    });
  }
});