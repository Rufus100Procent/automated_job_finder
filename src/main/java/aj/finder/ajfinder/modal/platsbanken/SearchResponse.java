package aj.finder.ajfinder.modal.platsbanken;

import lombok.Data;
import java.util.List;

import java.time.LocalDateTime;


@Data
public class SearchResponse {

    private Total total;
    private int positions;
    private int query_time_in_millis;
    private int result_time_in_millis;
    private List<Stat> stats;
    private FreetextConcepts freetext_concepts;
    private List<Hit> hits;

    @Data
    public static class Total {
        private int value;
    }

    @Data
    public static class Stat {
        private String type;
        private List<StatValue> values;
    }

    @Data
    public static class StatValue {
        private String term;
        private String concept_id;
        private String code;
        private int count;
    }

    @Data
    public static class FreetextConcepts {
        private List<String> skill;
        private List<String> occupation;
        private List<String> location;
        private List<String> skill_must;
        private List<String> occupation_must;
        private List<String> location_must;
        private List<String> skill_must_not;
        private List<String> occupation_must_not;
        private List<String> location_must_not;
    }

    @Data
    public static class Hit {
        private String id;
        private String external_id;
        private String original_id;
        private String label;
        private Object webpage_url;
        private String logo_url;
        private String headline;
        private LocalDateTime application_deadline;
        private int number_of_vacancies;
        private Description description;
        private JobAd.EmploymentType employment_type;
        private JobAd.SalaryType salary_type;
        private String salary_description;
        private JobAd.DurationInfo duration;
        private JobAd.WorkingHoursType working_hours_type;
        private JobAd.ScopeOfWork scope_of_work;
        private String access;
        private JobAd.Employer employer;
        private JobAd.ApplicationDetails application_details;
        private boolean experience_required;
        private boolean access_to_own_car;
        private boolean driving_license_required;
        private List<DrivingLicense> driving_license;
        private JobAd.Occupation occupation;
        private JobAd.OccupationGroup occupation_group;
        private JobAd.OccupationField occupation_field;
        private JobAd.WorkplaceAddress workplace_address;
        private LocalDateTime publication_date;
        private LocalDateTime last_publication_date;
        private boolean removed;
        private LocalDateTime removed_date;
        private String source_type;
        private long timestamp;
        private int relevance;
        private List<ApplicationContacts> application_contacts;
    }

    @Data
    public static class Description {
        private String text;
        private String text_formatted;
        private String company_information;
        private String needs;
        private String requirements;
        private String conditions;
    }

    @Data
    public static class DrivingLicense {
        private String concept_id;
        private String label;
        private String legacy_ams_taxonomy_id;
    }

    @Data
    public static class ApplicationContacts {
        private String name;
        private String description;
        private String email;
        private String telephone;
        private String contact_type;
    }

}


