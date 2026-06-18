package com.example.helloworld.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class User {
    @TableField(exist = false)
    private List<Topic> Topics;
    
    @TableField(exist = false)
    private Boolean following; // 当前登录用户是否关注了该用户

    // 用户ID（主键，自增）
    @TableId(type = IdType.AUTO) // 主键策略：自增
    private Long id;

    // 用户名（唯一）,也就是account，用户账号
    @TableField("username")
    private String username;

    // 密码（加密存储）
    @TableField("password")
    private String password;

    // 邮箱（唯一）
    @TableField("email")
    private String email;

    // 手机号
    @TableField("phone")
    private String phone;

    // 昵称
    @TableField("nickname")
    private String nickname;

    // 头像URL
    @TableField("avatar")
    private String avatar;

    // 性别：0-未知 1-男 2-女
    @TableField("gender")
    private Byte gender;

    // 生日
    @TableField("birthday")
    private Date birthday;

    // 个人简介
    @TableField("bio")
    private String bio;

    // 账户余额（元）
    @TableField("balance")
    private BigDecimal balance;

    // 是否是会员：0-否 1-是
    @TableField("is_vip")
    private Boolean isVip; // 数据库TINYINT(1)对应Java Boolean

    // 会员到期时间
    @TableField("vip_expire_time")
    private Date vipExpireTime;

    // 关注人数
    @TableField("follow_count")
    private Integer followCount;

    // 粉丝人数
    @TableField("fans_count")
    private Integer fansCount;

    // 发帖数量
    @TableField("post_count")
    private Integer postCount;

    // 获赞数量
    @TableField("like_count")
    private Integer likeCount;

    // 评论数量
    @TableField("comment_count")
    private Integer commentCount;

    // 垃圾评论数量
    @TableField("spam_comment_count")
    private Integer spamCommentCount;

    // 主页访问量
    @TableField("visit_count")
    private Integer visitCount;

    // 是否为劣迹用户：0-否 1-是
    @TableField("is_spam_user")
    private Boolean isSpamUser; // 数据库TINYINT(1)对应Java Boolean

    // 状态：0-禁用 1-正常
    @TableField("status")
    private Byte status;

    // 最后登录时间
    @TableField("last_login_time")
    private Date lastLoginTime;

    // 创建时间（MyBatis-Plus自动填充）
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;

    // 更新时间（MyBatis-Plus自动填充）
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;


    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", nickname='" + nickname + '\'' +
                ", avatar='" + avatar + '\'' +
                ", gender=" + gender +
                ", birthday=" + birthday +
                ", bio='" + bio + '\'' +
                ", balance=" + balance +
                ", isVip=" + isVip +
                ", vipExpireTime=" + vipExpireTime +
                ", followCount=" + followCount +
                ", fansCount=" + fansCount +
                ", postCount=" + postCount +
                ", likeCount=" + likeCount +
                ", commentCount=" + commentCount +
                ", spamCommentCount=" + spamCommentCount +
                ", visitCount=" + visitCount +
                ", isSpamUser=" + isSpamUser +
                ", status=" + status +
                ", lastLoginTime=" + lastLoginTime +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Byte getGender() {
        return gender;
    }

    public void setGender(Byte gender) {
        this.gender = gender;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public List<Topic> getTopics() {
        return Topics;
    }

    public void setTopics(List<Topic> topics) {
        Topics = topics;
    }

    public Boolean getFollowing() {
        return following;
    }

    public void setFollowing(Boolean following) {
        this.following = following;
    }

    public Boolean getIsVip() {
        return isVip;
    }

    public Date getVipExpireTime() {
        return vipExpireTime;
    }

    public void setIsVip(Boolean isVip) {
        this.isVip = isVip;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public void setVipExpireTime(Date vipExpireTime) {
        this.vipExpireTime = vipExpireTime;
    }

    public Integer getFollowCount() {
        return followCount;
    }

    public void setFollowCount(Integer followCount) {
        this.followCount = followCount;
    }

    public Integer getFansCount() {
        return fansCount;
    }

    public void setFansCount(Integer fansCount) {
        this.fansCount = fansCount;
    }

    public Integer getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(Integer likeCount) {
        this.likeCount = likeCount;
    }

    public Integer getPostCount() {
        return postCount;
    }

    public void setPostCount(Integer postCount) {
        this.postCount = postCount;
    }

    public Integer getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(Integer commentCount) {
        this.commentCount = commentCount;
    }

    public Integer getSpamCommentCount() {
        return spamCommentCount;
    }

    public void setSpamCommentCount(Integer spamCommentCount) {
        this.spamCommentCount = spamCommentCount;
    }

    public Integer getVisitCount() {
        return visitCount;
    }

    public void setVisitCount(Integer visitCount) {
        this.visitCount = visitCount;
    }

    public Boolean getSpamUser() {
        return isSpamUser;
    }

    public void setSpamUser(Boolean spamUser) {
        isSpamUser = spamUser;
    }

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
    }

    public Date getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(Date lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
