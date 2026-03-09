const app = getApp();

Page({
  data: {
    historyList: [],
    isLoading: false,
    hasLogin: false,
    marquee: {
      speed: 50,
      loop: -1,
      delay: 0
    }
  },
  onShow() {
    if (typeof this.getTabBar === 'function' && this.getTabBar()) {
      this.getTabBar().init();
    }
    if (app.globalData.openid) {
      this.setData({ hasLogin: true });
      this.fetchHistoryList();
    } else {
      this.setData({ hasLogin: false, historyList: [] });
    }
  },
  goToProfile() {
    wx.switchTab({ url: '/pages/profile/profile' });
  },
  goToIndex() {
    wx.switchTab({ url: '/pages/index/index' });
  },
  fetchHistoryList() {
    this.setData({ isLoading: true });
    wx.request({
      url: `${app.globalData.baseUrl}/api/history/list`,
      method: 'GET',
      data: { openid: app.globalData.openid },
      success: (res) => {
        if (res.data.code === 200) {
          const list = res.data.data.map(item => {
             if(item.createTime) {
                // 展示完整时间 YYYY-MM-DD HH:mm:ss
                item.timeStr = item.createTime.replace('T', ' ').substring(0, 19);
             }
             return item;
          });
          this.setData({ historyList: list });
        }
      },
      complete: () => {
        this.setData({ isLoading: false });
      }
    });
  },
  previewImage(e) {
    const url = e.currentTarget.dataset.url;
    wx.previewImage({ urls: [url], current: url });
  },
  onDelete(e) {
    const id = e.currentTarget.dataset.id;
    wx.showModal({
      title: '确认删除',
      content: '是否确认删除该记录？',
      confirmColor: '#d52828',
      success: (res) => {
        if (res.confirm) {
          wx.showLoading({ title: '删除中...', mask: true });
          wx.request({
            url: `${app.globalData.baseUrl}/api/history/${id}`,
            method: 'DELETE',
            success: (resp) => {
              if(resp.data.code === 200) {
                wx.showToast({ title: '删除成功', icon: 'success' });
                this.fetchHistoryList(); 
              } else {
                wx.showToast({ title: '删除失败', icon: 'none' });
              }
            },
            complete: () => wx.hideLoading()
          });
        }
      }
    });
  }
});