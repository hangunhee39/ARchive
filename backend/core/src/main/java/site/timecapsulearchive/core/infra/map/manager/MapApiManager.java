package site.timecapsulearchive.core.infra.map.manager;

import site.timecapsulearchive.core.infra.map.data.dto.AddressData;

public interface MapApiManager {

    /**
     * 경도와 위도를 받아서 주소를 반환한다.
     *
     * @param longitude 경도
     * @param latitude  위도
     * @return 위도와 경도로부터 반환된 주소
     */
    AddressData reverseGeoCoding(Double longitude, Double latitude);
}
