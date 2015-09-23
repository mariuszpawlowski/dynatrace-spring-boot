package demo;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Reservation {
	
	@Id
	@GeneratedValue
	private Long id;
	
	private String reservationName;
	
	public Reservation(String reservationName) {
		this.reservationName = reservationName;
	}
	
	public Reservation(){ // why JPA why ??
		
	}
	
	public Long getId() {
		return id;
	}

	public String getReservationName() {
		return reservationName;
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder("Reservaion{");
		builder.append("id=" + id);
		builder.append(",reservationName='" + reservationName).append("\'");
		builder.append("}");
		return builder.toString();
	}
	
	
	
}
