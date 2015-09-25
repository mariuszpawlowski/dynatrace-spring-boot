package demo;

import java.util.Arrays;
import java.util.Collection;

import javax.cache.annotation.CacheResult;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.ExportMetricWriter;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.metrics.jmx.JmxMetricWriter;
import org.springframework.boot.actuate.metrics.writer.MetricWriter;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.jmx.export.MBeanExporter;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vaadin.annotations.Theme;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Table;
import com.vaadin.ui.UI;

@EnableCaching
@SpringBootApplication
public class DemoApplication {

	@Bean
	CommandLineRunner dummy(ReservationRepository r) {
		return strings -> {
			Arrays.asList("jakub,thomas,peter,josh,juergen".split(",")).forEach(n -> r.save(new Reservation(n)));

			r.findAll().forEach(System.out::println);
			r.findByReservationName("jakub").forEach(System.out::println);
		};
	}

	@Controller
	public static class ReservationMvcController {

		@RequestMapping("/page")
		String page(Model model) {
			model.addAttribute("reservations", this.reservationRepository.findAll());
			return "reservations";
		}

		@Autowired
		ReservationRepository reservationRepository;
	}

	@Service
	public static class ReservationService {

		@Autowired
		private ReservationRepository reservationRepository;

		@CacheResult
		public Collection<Reservation> getReservations() throws InterruptedException {
			Thread.sleep(1000 * 5);
			return this.reservationRepository.findAll();
		}
	}

	@RestController
	public static class ReservationRestController {

		@Autowired
		private ReservationRepository reservationRepository;

		@RequestMapping("/reservations")
		Collection<Reservation> reservations() {
			return this.reservationRepository.findAll();
		}
	}

	@Named
	@ApplicationPath("/jersey")
	public static class JerseyConfig extends ResourceConfig {
		public JerseyConfig() {
			register(JacksonFeature.class);
			register(ReservationEndpoint.class);
		}
	}

	@Named
	@Path("/reservations")
	public static class ReservationEndpoint {

		@Inject
		private ReservationRepository reservationRepository;

		@Inject
		private ReservationService reservationService;

		@GET
		@Produces(MediaType.APPLICATION_JSON_VALUE)
		public Collection<Reservation> reservations() throws InterruptedException {
			// return this.reservationRepository.findAll();
			return this.reservationService.getReservations();
		}
	}

	@SpringUI(path = "/ui")
	@Theme("valo")
	public static class ReservationUI extends UI {

		@Autowired
		private ReservationRepository reservationRepository;

		@Override
		protected void init(VaadinRequest request) {
			Table t = new Table();
			t.setContainerDataSource(new BeanItemContainer<Reservation>(Reservation.class, this.reservationRepository.findAll()));
			t.setSizeFull();
			setContent(t);

		}

	}

	@Bean
	HealthIndicator healthIndicator() {
		return () -> Health.status("Status OK").build();
	}

	@Bean
	@ExportMetricWriter
	@ConditionalOnProperty("spring.jmx.enabled")
	MetricWriter metricWriter(@Qualifier("mbeanExporter") MBeanExporter exporter) {
		return new JmxMetricWriter(exporter);
	}

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}
}
