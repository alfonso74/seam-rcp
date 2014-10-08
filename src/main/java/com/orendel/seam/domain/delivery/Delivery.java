package com.orendel.seam.domain.delivery;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;


@Entity
@Table(name = "AM_DELIVERY")
public class Delivery {
	
	/** Delivery database id */
	@Id
	@GeneratedValue
	@Column(name = "ID")
	private Long id;
	
	/** Invoice database id */
	@Column(name = "DOC_ID")
	private Long invoiceId;
	
	/** Invoice number (CounterPoint document number) */
	@Column(name = "TKT_NO")
	private String ticketNumber;
	
	/** Delivery created date */
	@Column(name = "CREATED")
	private Date created;
	
	@Column(name = "USERNAME")
	private String userName;
	
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "delivery", fetch = FetchType.EAGER)
	@Fetch(value = FetchMode.SUBSELECT)
    private List<DeliveryLine> deliveryLines;
	

	public Delivery() {
	}

	
	// ****************************** Helper methods ********************************
	
	public void addDeliveryLine(DeliveryLine line) {
		line.setDelivery(this);
		getDeliveryLines().add(line);
	}
	
	
	public int getDeliveredItemsTotalQty() {
		int total = 0;
		for (DeliveryLine line : getDeliveryLines()) {
			total += line.getQtyDelivered().intValue();
		}
		return total;
	}
	
	
	
	// ***************************** Getters and setters ********************************
	
	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}


	public Long getInvoiceId() {
		return invoiceId;
	}


	public void setInvoiceId(Long invoiceId) {
		this.invoiceId = invoiceId;
	}


	public String getTicketNumber() {
		return ticketNumber;
	}


	public void setTicketNumber(String ticketNumber) {
		this.ticketNumber = ticketNumber;
	}


	public Date getCreated() {
		return created;
	}


	public void setCreated(Date created) {
		this.created = created;
	}


	public String getUserName() {
		return userName;
	}


	public void setUserName(String userName) {
		this.userName = userName;
	}


	public List<DeliveryLine> getDeliveryLines() {
		if (deliveryLines == null) {
			this.deliveryLines = new ArrayList<DeliveryLine>();
		}
		return deliveryLines;
	}


	public void setDeliveryLines(List<DeliveryLine> deliveryLines) {
		this.deliveryLines = deliveryLines;
	}
	
}
