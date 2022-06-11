package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;

@Embeddable
public class Sections {
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "line_id", foreignKey = @ForeignKey(name = "fk_line_station_to_line"))
    private List<Section> sections = new ArrayList<>();

    public void add(final Section section) {
        validateDuplicate(section);
        sections.stream()
                .filter(origin -> origin.intersects(section))
                .findFirst()
                .ifPresent(origin -> origin.calculate(section));

        sections.add(section);
    }

    private void validateDuplicate(final Section section) {
        if (sections.stream().anyMatch(origin -> origin.equalsStations(section))) {
            throw new SectionException("이미 등록되어 있는 구간입니다.");
        }
    }

    public Set<Station> getStationsOrderBy() {
        final LinkedHashSet<Station> hashSet = new LinkedHashSet<>();
        Station station = findFirstStation();
        hashSet.add(station);

        while (hashSet.size() <= sections.size()) {
            final Section nowSection = findSectionByUpStation(station);
            hashSet.add(nowSection.getDownStation());
            station = nowSection.getDownStation();
        }

        return hashSet;
    }

    private Section findSectionByUpStation(final Station station) {
        return sections.stream()
                .filter(section -> section.hasUpStation(station))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }

    private Station findFirstStation() {
        return getStations().stream()
                .filter(this::noneHasDownStation)
                .findFirst()
                .orElseThrow(IllegalStateException::new);
    }

    private boolean noneHasDownStation(final Station station) {
        return sections.stream().noneMatch(section -> section.hasDownStation(station));
    }

    private Set<Station> getStations() {
        return sections.stream()
                .flatMap(section -> section.getStations().stream())
                .collect(Collectors.toSet());
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Sections)) {
            return false;
        }
        final Sections that = (Sections) o;
        return Objects.equals(sections, that.sections);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sections);
    }

    @Override
    public String toString() {
        return "Sections{" +
                "sections=" + sections +
                '}';
    }
}