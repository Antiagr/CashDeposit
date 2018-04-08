package com.example.anti.cashdeposits.database;

public class DepositDBSchema {

    public static final class DepositTable{
        public static final String NAME = "deposit";

        public static final class Cols{
            public static final String UUID = "deposit_uuid";
            public static final String TITLE = "deposit_title";
            public static final String SUMM = "deposit_summ";
            public static final String CURRENCY_ID = "currency_id";
            public static final String DATE = "deposit_date";
            public static final String TIME = "deposit_time";
            public static final String PERCENTAGE = "deposit_percentage";
            public static final String BANK_ID = "bank_id";
            public static final String INVESTOR_ID = "investor_id";
        }
    }

    public static final class DepositProfitsTable{
        public static final  String NAME = "deposit_profits";

        public static final class Cols{
            public static final String UUID = "profit_uuid";
            public static final String DEPOSIT_UUID = "deposit_uuid";
            public static final String DATE = "profit_date";
            public static final String VALUE = "profit_value";
            public static final String PROFIT = "profit";
            public static final String MONTH_PROFIT = "month_profit";
        }
    }

    public static final class BankTable{
        public static final String NAME = "bank";

        public static final class Cols{
            public static final String UUID = "bank_uuid";
            public static final String TITLE = "bank_title";
        }
    }

    public static final class InvestorTable{
        public static final String NAME = "investor";

        public static final class Cols{
            public static final String UUID = "investor_uuid";
            public static final String NAME  = "investor_name";
        }
    }

    public static final class CurrencyTable{
        public static final String NAME = "currency";

        public static final class Cols{
            public static final String UUID = "currency_uuid";
            public static final String TITLE = "currency_title";
        }
    }

    public static final class CurrencyDynamicTable{
        public static final String NAME = "currency_dynamic";

        public static final class Cols{
            public static final String ID = "id";
            public static final String CURRENCY_ID = "currency_id";
            public static final String DATE = "currency_date";
            public static final String RATE = "currency_rate";
        }
    }
}
