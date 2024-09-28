package zerobase.weather.service;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import zerobase.weather.WeatherApplication;
import zerobase.weather.domain.DateWeather;
import zerobase.weather.domain.Diary;
import zerobase.weather.repository.DateWeatherRepository;
import zerobase.weather.repository.DiaryRepository;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class DiaryService {

    @Value("${openweathermap.key}")
    private  String apiKey;

    private final DiaryRepository diaryRepository;

    private final DateWeatherRepository dateWeatherRepository;

    private static final Logger logger = LoggerFactory.getLogger(WeatherApplication.class);

    public DiaryService(DiaryRepository diaryRepository, DateWeatherRepository dateWeatherRepository) {
        this.diaryRepository = diaryRepository;
        this.dateWeatherRepository = dateWeatherRepository;
    }

    @Transactional
    @Scheduled(cron = "0 0 1 * * *")    // 매일 1시 0분 0초
    public void saveWeatherDate() {
        logger.info("[DiaryService::saveWeatherDate] Scheduler Stared");
        dateWeatherRepository.save(getWeatherFormApi());
        logger.info("[DiaryService::saveWeatherDate] Scheduler End");
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void createDiary(LocalDate date, String text){
        logger.info("[DiaryService::createDiary] Stared");

        // 날씨 데이터 가져오기 ( API에서 가져오기? or DB에서 가져오기?)
        DateWeather dateWeather = getDateWeather(date);

        // 파싱된 데이터 + 일기 값 DB에 넣기
        Diary nowDiary = new Diary();
        nowDiary.setDateWeater(dateWeather);
        nowDiary.setText(text);


        logger.info("[DiaryService::createDiary] save Date [{}}", nowDiary.toString());
        diaryRepository.save(nowDiary);

        logger.info("[DiaryService::createDiary] end");
    }

    private DateWeather getWeatherFormApi() {
        logger.info("[DiaryService::getWeatherFormApi] Started");

        // open weather map에서 날씨 데이터 가져오기
        String weatherData = getWeatherString();

        // 받아온 날씨 json 파싱하기
        Map<String, Object> parsedWeather = parseWeather(weatherData);

        // 파싱된 데이터 + 날씨 값 넣기
        DateWeather dateWeather = new DateWeather();
        dateWeather.setDate(LocalDate.now());
        dateWeather.setWeather(parsedWeather.get("main").toString());
        dateWeather.setIcon(parsedWeather.get("icon").toString());
        dateWeather.setTemperature((Double) parsedWeather.get("temp"));

        logger.info("[DiaryService::getWeatherFormApi] return Data [{}]", dateWeather.toString());

        return dateWeather;
    }

    private DateWeather getDateWeather(LocalDate date) {
        logger.info("[getDateWeather::getDateWeather] started");
        List<DateWeather> dateWeatherListFromDB = dateWeatherRepository.findAllByDate(date);

        if(dateWeatherListFromDB.isEmpty()) {
            // 새로 API에서 날씨 정볼를 가져온다.
            // 정책에 의해 " 현재 날씨를 가져오거나 날씨 없이 일기를 쓰도록 진행 "
            return getWeatherFormApi();
        } else {
            logger.info("[getDateWeather::getDateWeather] get DataBase");
            return dateWeatherListFromDB.get(0);
        }
    }

    @Transactional(readOnly = true)
    public List<Diary> readDiary (LocalDate date) {
        logger.info("[getDateWeather::readDiary] started, date [{}]", date.toString());
        return diaryRepository.findAllByDate(date);
    }

    @Transactional(readOnly = true)
    public List<Diary> readDiaries (LocalDate startDate, LocalDate endDate) {
        logger.info("[getDateWeather::readDiaries] Stared, {} ~ {}", startDate.toString(), endDate.toString());
        return diaryRepository.findAllByDateBetween(startDate, endDate);
    }

    public void updateDiary (LocalDate date, String text) {
        logger.info("[getDateWeather::updateDiary] Stared. update date [{}], Text [{}]", date, text);
        Diary nowDiary = diaryRepository.getFirstByDate(date);
        nowDiary.setText(text);
        logger.info("[getDateWeather::updateDiary] save data {}", nowDiary);
        diaryRepository.save(nowDiary);
        logger.info("[getDateWeather::updateDiary] End.");
    }

    public void deleteDiary (LocalDate date) {
        logger.info("[getDateWeather::deleteDiary] Startd. delete Date [{}]", date);
        Diary nowDiary = diaryRepository.getFirstByDate(date);
        diaryRepository.deleteAllByDate(nowDiary.getDate());
    }

    private String getWeatherString() {
        logger.info("[getDateWeather::getWeatherString] Startd");
        String apiUrl = "https://api.openweathermap.org/data/2.5/weather?q=seoul&appid=" + apiKey;

        try {
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            BufferedReader br;
            if(responseCode == 200) {
                br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            } else {
                br = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
            }

            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = br.readLine()) != null) {
                response.append(inputLine);
            }
            br.close();

            logger.info("[getDateWeather::getWeatherString] success. Date : {}", response.toString());
            return  response.toString();
        } catch (Exception e) {
            logger.info("[getDateWeather::getWeatherString] fail.");
            return "faild to get response";
        }
    }

    private Map<String, Object> parseWeather(String jsonString) {
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject;

        try {
            jsonObject = (JSONObject) jsonParser.parse(jsonString);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        Map<String, Object> resultMap = new HashMap<>();

        JSONObject mainData = (JSONObject) jsonObject.get("main");
        resultMap.put("temp", mainData.get("temp"));
        JSONArray weatherArray = (JSONArray) jsonObject.get("weather");
        JSONObject weatherData = (JSONObject) weatherArray.get(0);
        resultMap.put("main", weatherData.get("main"));
        resultMap.put("icon", weatherData.get("icon"));

        return resultMap;
    }
}
