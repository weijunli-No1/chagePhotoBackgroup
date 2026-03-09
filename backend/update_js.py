import re

with open('/Users/liweijun/vscode/chagePhotoBackgroup/mini-program/pages/profile/profile.js', 'r') as f:
    content = f.read()

new_methods = """  navToPrivacy() {
    wx.navigateTo({
      url: '/pages/document/document?type=privacy'
    });
  },

  navToAgreement() {
    wx.navigateTo({
      url: '/pages/document/document?type=agreement'
    });
  }"""

content = re.sub(
    r'  showPrivacy\(\) \{.*?\n  },\n\n  showDisclaimer\(\) \{.*?\n  \}',
    new_methods,
    content,
    flags=re.DOTALL
)

with open('/Users/liweijun/vscode/chagePhotoBackgroup/mini-program/pages/profile/profile.js', 'w') as f:
    f.write(content)
