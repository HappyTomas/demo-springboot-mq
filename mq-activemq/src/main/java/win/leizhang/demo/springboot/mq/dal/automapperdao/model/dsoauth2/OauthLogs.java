package win.leizhang.demo.springboot.mq.dal.automapperdao.model.dsoauth2;

import java.util.Date;
import javax.persistence.*;

@Table(name = "oauth2_logs")
public class OauthLogs {
    /**
     * 主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 应用id
     */
    @Column(name = "app_id")
    private String appId;

    /**
     * openid
     */
    @Column(name = "open_id")
    private String openId;

    /**
     * 创建时间
     */
    @Column(name = "created_time")
    private Date createdTime;

    /**
     * 创建者
     */
    @Column(name = "created_by")
    private String createdBy;

    /**
     * 更新时间
     */
    @Column(name = "updated_time")
    private Date updatedTime;

    /**
     * 更新者
     */
    @Column(name = "updated_by")
    private String updatedBy;

    /**
     * 0:未删除，1：已删除。
     */
    @Column(name = "is_deleted")
    private Integer isDeleted;

    @Column(name = "opt_counter")
    private Integer optCounter;

    /**
     * 内容正文
     */
    private String content;

    /**
     * 获取主键
     *
     * @return id - 主键
     */
    public Long getId() {
        return id;
    }

    /**
     * 设置主键
     *
     * @param id 主键
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 获取应用id
     *
     * @return app_id - 应用id
     */
    public String getAppId() {
        return appId;
    }

    /**
     * 设置应用id
     *
     * @param appId 应用id
     */
    public void setAppId(String appId) {
        this.appId = appId;
    }

    /**
     * 获取openid
     *
     * @return open_id - openid
     */
    public String getOpenId() {
        return openId;
    }

    /**
     * 设置openid
     *
     * @param openId openid
     */
    public void setOpenId(String openId) {
        this.openId = openId;
    }

    /**
     * 获取创建时间
     *
     * @return created_time - 创建时间
     */
    public Date getCreatedTime() {
        return createdTime;
    }

    /**
     * 设置创建时间
     *
     * @param createdTime 创建时间
     */
    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    /**
     * 获取创建者
     *
     * @return created_by - 创建者
     */
    public String getCreatedBy() {
        return createdBy;
    }

    /**
     * 设置创建者
     *
     * @param createdBy 创建者
     */
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * 获取更新时间
     *
     * @return updated_time - 更新时间
     */
    public Date getUpdatedTime() {
        return updatedTime;
    }

    /**
     * 设置更新时间
     *
     * @param updatedTime 更新时间
     */
    public void setUpdatedTime(Date updatedTime) {
        this.updatedTime = updatedTime;
    }

    /**
     * 获取更新者
     *
     * @return updated_by - 更新者
     */
    public String getUpdatedBy() {
        return updatedBy;
    }

    /**
     * 设置更新者
     *
     * @param updatedBy 更新者
     */
    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    /**
     * 获取0:未删除，1：已删除。
     *
     * @return is_deleted - 0:未删除，1：已删除。
     */
    public Integer getIsDeleted() {
        return isDeleted;
    }

    /**
     * 设置0:未删除，1：已删除。
     *
     * @param isDeleted 0:未删除，1：已删除。
     */
    public void setIsDeleted(Integer isDeleted) {
        this.isDeleted = isDeleted;
    }

    /**
     * @return opt_counter
     */
    public Integer getOptCounter() {
        return optCounter;
    }

    /**
     * @param optCounter
     */
    public void setOptCounter(Integer optCounter) {
        this.optCounter = optCounter;
    }

    /**
     * 获取内容正文
     *
     * @return content - 内容正文
     */
    public String getContent() {
        return content;
    }

    /**
     * 设置内容正文
     *
     * @param content 内容正文
     */
    public void setContent(String content) {
        this.content = content;
    }
}