package com.vsn.config.rest.resources;

public enum Statuses {
    Ok(0), InvalidCredentials(1), Invalid2FA(2), LoginTaken(3), EmailTaken(4), AuthenticationFailed(5), ModelMalformed(6),
    UnexpectedError(7), TokenNotCreated(8), WeakPassword(9), InvalidHash(10), InvalidUserData(11), UserNotExists(12),
    InvalidExtension(13), UploadFileIsEmpty(14), InvalidToken(15), NotEnoughCredits(16), SubscriptionExpired(17),
    TicketsAmountExceeded(18), BadRole(19), FileNotFound(20), TutorialCompleted(21), ModuleDisabled(22), TicketNotExists(23),
    TicketNotUpdatable(24), AuthorizationFailed(25), InvalidImage(26), TestNotExists(27), TelegramNotConnected(28),
    TelegramAlreadyConnected(29), IdentifierNotFound(30), BannedIp(31), LimitReached(32), UnauthorizedRequest(33),
    WrongStockApiKeys(34), EmptyStocks(35), EmailNotConfirmed(36), NotEnoughEthers(37), PageNotExist(38), AddressNotExist(39),
    PairNotExists(40), NotificationNotSelected(41), NoFreeSlots(42), AnalyzerEnabled(43), AnalyzerDisabled(44), AnalyzerNotConfigured(45),
    StockNotConnected(46), NewsAnalyzerSettingsNotFound(47), PorfolioManagerIncorrectResponse(48), TradeForecastNotFound(49), PairNotSupported(50),
    NotSubscribed(51), PriceNotificationNotFound(52), SupportTimeout(53), PriceAnalyzerMinStocks(54), TimeframeNotAvailable(55), UnsupportedStockForModule(56), SettingsNotExists(57), SubscriptionAlreadyPresent(58),
    SameExchangeApiKeys(59), RegistrationFailed(60), NotImplemented(61), RequiredMinForStopLossOrTakeProfit(62);

    private int status;

    Statuses(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }
}
