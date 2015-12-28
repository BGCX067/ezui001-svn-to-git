package cn.vstore.appserver.model;

import java.io.Serializable;
import java.util.Date;

public class OrderRefundHistory implements Serializable{

	/**
	 * 取消訂單的退款記錄
	 */
	private static final long serialVersionUID = 2545078239254466454L;
	private Long id;
	private Long orderId;
	private Date applyRefundDate;
	private String applyRefundReason;
	private int approvedStatus;
	private Date approvedDate;
	private Long approvedUid;
	private String approvedReason;
	private double refundAmount;
	private int refundMonth;
	private Date oldRightEnddate;
	private Date newRightEnddate;
	private Date transferDate;
	private double transferAmount;
	private Date transferLogDate;
	private Long transferLogUid;
	private String transferLogMemo;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getOrderId() {
		return orderId;
	}
	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}
	public Date getApplyRefundDate() {
		return applyRefundDate;
	}
	public void setApplyRefundDate(Date applyRefundDate) {
		this.applyRefundDate = applyRefundDate;
	}
	public String getApplyRefundReason() {
		return applyRefundReason;
	}
	public void setApplyRefundReason(String applyRefundReason) {
		this.applyRefundReason = applyRefundReason;
	}
	public int getApprovedStatus() {
		return approvedStatus;
	}
	public void setApprovedStatus(int approvedStatus) {
		this.approvedStatus = approvedStatus;
	}
	public Date getApprovedDate() {
		return approvedDate;
	}
	public void setApprovedDate(Date approvedDate) {
		this.approvedDate = approvedDate;
	}
	public Long getApprovedUid() {
		return approvedUid;
	}
	public void setApprovedUid(Long approvedUid) {
		this.approvedUid = approvedUid;
	}
	public String getApprovedReason() {
		return approvedReason;
	}
	public void setApprovedReason(String approvedReason) {
		this.approvedReason = approvedReason;
	}
	public double getRefundAmount() {
		return refundAmount;
	}
	public void setRefundAmount(double refundAmount) {
		this.refundAmount = refundAmount;
	}
	public Date getOldRightEnddate() {
		return oldRightEnddate;
	}
	public void setOldRightEnddate(Date oldRightEnddate) {
		this.oldRightEnddate = oldRightEnddate;
	}
	public Date getNewRightEnddate() {
		return newRightEnddate;
	}
	public void setNewRightEnddate(Date newRightEnddate) {
		this.newRightEnddate = newRightEnddate;
	}
	public Date getTransferDate() {
		return transferDate;
	}
	public void setTransferDate(Date transferDate) {
		this.transferDate = transferDate;
	}
	public double getTransferAmount() {
		return transferAmount;
	}
	public void setTransferAmount(double transferAmount) {
		this.transferAmount = transferAmount;
	}
	public Date getTransferLogDate() {
		return transferLogDate;
	}
	public void setTransferLogDate(Date transferLogDate) {
		this.transferLogDate = transferLogDate;
	}
	public Long getTransferLogUid() {
		return transferLogUid;
	}
	public void setTransferLogUid(Long transferLogUid) {
		this.transferLogUid = transferLogUid;
	}
	public String getTransferLogMemo() {
		return transferLogMemo;
	}
	public void setTransferLogMemo(String transferLogMemo) {
		this.transferLogMemo = transferLogMemo;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public int getRefundMonth() {
		return refundMonth;
	}
	public void setRefundMonth(int refundMonth) {
		this.refundMonth = refundMonth;
	}
	
}
