App({
  globalData: {
    userInfo: null,
    openid: '',
    baseUrl: 'https://your-domain.com/api', // 后端 API 地址，可根据环境动态替换
    defaultRewardedVideoAdUnitId: 'your_ad_unit_id',
    rewardedVideoAdUnitId: ''
  },
  
  onLaunch: function () {
    // 检查缓存的特定信息
    const openid = wx.getStorageSync('openid');
    if (openid) {
      this.globalData.openid = openid;
    }

    this.loadRemoteAppConfig();
  },

  loadRemoteAppConfig: function() {
    wx.request({
      url: `${this.globalData.baseUrl}/api/app/config`,
      method: 'GET',
      success: (res) => {
        if (res.data && res.data.code === 200 && res.data.data) {
          this.globalData.rewardedVideoAdUnitId = res.data.data.rewardedVideoAdUnitId || '';
        }
      },
      fail: () => {
        this.globalData.rewardedVideoAdUnitId = '';
      }
    });
  },

  // 微信授权登录
  doLogin: function() {
    return new Promise((resolve, reject) => {
      wx.login({
        success: (res) => {
          if (res.code) {
            wx.request({
              url: `${this.globalData.baseUrl}/api/user/login`,
              method: 'POST',
              data: {
                code: res.code
              },
              header: {
                'content-type': 'application/x-www-form-urlencoded'
              },
              success: (response) => {
                if(response.data.code === 200) {
                  const data = response.data.data;
                  this.globalData.openid = data.openid;
                  wx.setStorageSync('openid', data.openid);
                  resolve(data);
                } else {
                  wx.showToast({ title: '登录失败', icon: 'none' });
                  reject(response.data);
                }
              },
              fail: (err) => {
                wx.showToast({ title: '网络异常', icon: 'none' });
                reject(err);
              }
            });
          } else {
            console.log('登录失败！' + res.errMsg);
            reject(res);
          }
        }
      });
    });
  }
})