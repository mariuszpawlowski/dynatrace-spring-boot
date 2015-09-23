package demo;

import java.awt.*;
import java.util.Arrays;
import java.util.Collection;

import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@SpringBootApplication
public class DemoApplication {
	
	@Bean
	CommandLineRunner dummy(ReservationRepository r){
		return strings -> {
			Arrays.asList("jakub,thomas,peter,josh,juergen".split(","))
			.forEach(n -> r.save(new Reservation(n)));

			r.findAll().forEach(System.out::println);

			r.findByReservationName("jakub").forEach(System.out::println);
		};
	}

	@RestController
	public static class ReservationRestController{

		@Autowired
		private ReservationRepository reservationRepository;

		@RequestMapping("/reservations")
		Collection<Reservation> reservations(){
			return this.reservationRepository.findAll();
		}
	} 

	@Named
	@ApplicationPath("/jersey")
	public static class JerseyConfig extends ResourceConfig{
		public JerseyConfig(){
			register(JacksonFeature.class);
			register(ReservationEndpoint.class);
		}
	}

	@Named
	@Path("/reservations")
	public static class ReservationEndpoint{

		@Inject
		private ReservationRepository reservationRepository;

		@GET
		@Produces(MediaType.APPLICATION_JSON_VALUE)
		public Collection<Reservation> reservations(){
			return this.reservationRepository.findAll();
		}
	}


    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }
}
