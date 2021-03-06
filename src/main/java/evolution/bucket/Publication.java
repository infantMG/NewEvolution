package evolution.bucket;


import com.fasterxml.jackson.annotation.JsonInclude;
import evolution.common.PublicationCategoryEnum;
import evolution.model.user.StandardUser;
import lombok.*;
import javax.persistence.*;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by Infant on 05.07.2017.
 */
@Entity
@Table(name = "publication")
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor @AllArgsConstructor @Getter @Setter @ToString
public class Publication {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_publication")
    @SequenceGenerator(name = "seq_publication", sequenceName = "seq_publication_id", allocationSize = 1)
    private Long id;

    @Column
    private String content;

    @Column(name = "date_publication")
    @Temporal(TemporalType.TIMESTAMP)
    private Date date;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "sender_id")
    private StandardUser sender;

    @Column(name = "category_id")
    private Long category;

    @Column
    private String subject;

    public Publication(Long id) {
        this.id = id;
    }

    public String getCategoryName() {
        List<PublicationCategoryEnum> list = Arrays.asList(PublicationCategoryEnum.values());
        for (PublicationCategoryEnum pce: list) {
            if (pce.getId() == category) {
                return pce.name();
            }
        }
        return null;
    }


}
