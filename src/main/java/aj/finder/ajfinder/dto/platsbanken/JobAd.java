package aj.finder.ajfinder.dto.platsbanken;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class JobAd {

    private String id;
    private String external_id;
    private String original_id;
    private String label;
    private String webpage_url;
    private String logo_url;
    private String headline;
    private LocalDateTime application_deadline;
    private int number_of_vacancies;
    private Description description;
    private EmploymentType employment_type;
    private SalaryType salary_type;
    private String salary_description;
    private DurationInfo duration;
    private WorkingHoursType working_hours_type;
    private ScopeOfWork scope_of_work;
    private Employer employer;
    private ApplicationDetails application_details;
    private boolean experience_required;
    private boolean access_to_own_car;
    private boolean driving_license_required;
    private String driving_license;
    private Occupation occupation;
    private OccupationGroup occupation_group;
    private OccupationField occupation_field;
    private WorkplaceAddress workplace_address;
    private LocalDateTime publication_date;
    private LocalDateTime last_publication_date;
    private boolean removed;
    private LocalDateTime removed_date;
    private String source_type;
    private Long timestamp;

    @Data
    public static class Description {
        private String text;
        private String text_formatted;
    }

    @Data
    public static class EmploymentType {
        private String concept_id;
        private String label;
        private String legacy_ams_taxonomy_id;
    }

    @Data
    public static class SalaryType {
        private String concept_id;
        private String label;
        private String legacy_ams_taxonomy_id;
    }

    @Data
    public static class DurationInfo {
        private String concept_id;
        private String label;
        private String legacy_ams_taxonomy_id;
    }

    @Data
    public static class WorkingHoursType {
        private String concept_id;
        private String label;
        private String legacy_ams_taxonomy_id;
    }

    @Data
    public static class ScopeOfWork {
        private int min;
        private int max;
    }

    @Data
    public static class Employer {
        private String phone_number;
        private String email;
        private String url;
        private String organization_number;
        private String name;
        private String workplace;
    }

    @Data
    public static class ApplicationDetails {
        private String information;
        private String reference;
        private String email;
        private boolean via_af;
        private String url;
        private String other;
    }

    @Data
    public static class Occupation {
        private String concept_id;
        private String label;
        private String legacy_ams_taxonomy_id;
    }

    @Data
    public static class OccupationGroup {
        private String concept_id;
        private String label;
        private String legacy_ams_taxonomy_id;
    }

    @Data
    public static class OccupationField {
        private String concept_id;
        private String label;
        private String legacy_ams_taxonomy_id;
    }

    @Data
    public static class WorkplaceAddress {
        private String municipality;
        private String municipality_code;
        private String municipality_concept_id;
        private String region;
        private String region_code;
        private String region_concept_id;
        private String country;
        private String country_code;
        private String country_concept_id;
        private String street_address;
        private String postcode;
        private String city;
        private Double[] coordinates;
    }

}


