package nextstep.subway.application;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.SectionRequest;
import nextstep.subway.dto.UpdateLineRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LineService {

    private final LineRepository lineRepository;

    private final StationRepository stationRepository;

    public LineService(final LineRepository lineRepository, final StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    @Transactional
    public LineResponse saveLine(final LineRequest lineRequest) {
        Line line = lineRequest.toLine();
        final Section section = new Section(upStation(lineRequest), downStation(lineRequest), lineRequest.getDistance());
        line.addSection(section);
        lineRepository.save(line);
        return LineResponse.of(line);
    }

    public List<LineResponse> findAllLines() {
        return lineRepository.findAll()
                .stream()
                .map(LineResponse::of)
                .collect(Collectors.toList());
    }

    public LineResponse findLine(final Long id) {
        return LineResponse.of(findLineById(id));
    }

    @Transactional
    public void updateLine(final Long id, final UpdateLineRequest updateLineRequest) {
        final Line line = findLineById(id);
        line.update(updateLineRequest.toLine());
    }

    private Line findLineById(final Long id) {
        return lineRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("지하철 노선을 찾을 수 없습니다."));
    }

    @Transactional
    public void deleteLine(final Long id) {
        lineRepository.deleteById(id);
    }

    @Transactional
    public void addSections(final Long id, final SectionRequest sectionRequest) {
        final Line line = findLineById(id);
        final Section section = new Section(upStation(sectionRequest), downStation(sectionRequest), sectionRequest.getDistance());
        line.addSection(section);
    }

    private Station upStation(final LineRequest lineRequest) {
        return findStationById(lineRequest.getUpStationId());
    }

    private Station upStation(final SectionRequest sectionRequest) {
        return findStationById(sectionRequest.getUpStationId());
    }

    private Station downStation(final LineRequest lineRequest) {
        return findStationById(lineRequest.getDownStationId());
    }

    private Station downStation(final SectionRequest sectionRequest) {
        return findStationById(sectionRequest.getDownStationId());
    }

    private Station findStationById(final long id) {
        return stationRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("지하철역을 찾을 수 없습니다."));
    }
}
