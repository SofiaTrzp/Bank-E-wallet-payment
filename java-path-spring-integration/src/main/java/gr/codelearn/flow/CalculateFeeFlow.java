package gr.codelearn.flow;

import com.google.common.base.Strings;
import gr.codelearn.service.AccountLookupService;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.messaging.MessageChannel;

import java.util.Map;

public class CalculateFeeFlow {
    private AccountLookupService accountLookupService;

    @Bean
    public MessageChannel calculationFeeChannel() {
        return new DirectChannel();
    }

    @Bean
    public IntegrationFlow calculateFeesInternalFlow(MessageChannel calculationFeeChannel, MessageChannel errorChannel) {
        return IntegrationFlows
                .from(calculationFeeChannel())
                .transform(accountLookupService::validate)
                .<Map<String, Object>, Boolean>route(m -> Strings.isNullOrEmpty((String) m.get("errorMessage")), message -> message
                        .channelMapping(true, calculationFeeChannel)
                        .channelMapping(false, errorChannel)
                )
                .get();

    }
}
