import org.springframework.web.client.RestTemplate;

public class IncomeFlexDepletionReportProcessor implements ItemProcessor<Participant, PptReportDto> {

    private double depletionThreshold;
    private RestTemplate restTemplate;

    public IncomeFlexDepletionReportProcessor(double depletionThreshold) {
        this.depletionThreshold = depletionThreshold;
        this.restTemplate = new RestTemplate();
    }

    @Override
    public PptReportDto process(Participant participant) throws Exception {
        PptReportDto reportDto = new PptReportDto();

        // Make an API call to retrieve the LAWA amount and other field values
        String apiUrl = "https://example.com/api/participant/" + participant.getId();
        ParticipantApiData apiData = restTemplate.getForObject(apiUrl, ParticipantApiData.class);

        if (apiData != null) {
            // Set the values from the API response to the reportDto
            reportDto.setPlanNumber(apiData.getPlanNumber());
            reportDto.setPlanName(apiData.getPlanName());
            reportDto.setSsn(apiData.getSsn());
            reportDto.setSsnExtension(apiData.getSsnExtension());
            reportDto.setPptFirstName(apiData.getPptFirstName());
            reportDto.setPptLastName(apiData.getPptLastName());
            reportDto.setProductVersion(apiData.getProductVersion());
            reportDto.setCurrentAccountBalance(apiData.getCurrentAccountBalance());
            reportDto.setBalanceEffectiveDate(apiData.getBalanceEffectiveDate());
            reportDto.setLawaAmount(apiData.getLawaAmount());
            reportDto.setIncomeBase(apiData.getIncomeBase());
            reportDto.setYtdWithdrawalAmount(apiData.getYtdWithdrawalAmount());
            reportDto.setDob(apiData.getDob());
            reportDto.setInstallmentPaymentAmounts(apiData.getInstallmentPaymentAmounts());
            reportDto.setInstallmentPaymentFrequency(apiData.getInstallmentPaymentFrequency());

            // Check if the market value is nearing depletion
            double marketValue = apiData.getMarketValue();
            double depletionThresholdValue = apiData.getLawaAmount() * 2;
            if (marketValue <= depletionThresholdValue) {
                return reportDto; // Return the reportDto if the market value is nearing depletion
            }
        }

        return null; // Return null if the participant is not eligible for the report or API call fails
    }
}
