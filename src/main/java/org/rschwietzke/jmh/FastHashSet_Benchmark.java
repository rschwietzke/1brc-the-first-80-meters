package org.rschwietzke.jmh;

import java.util.concurrent.TimeUnit;
import java.util.random.RandomGeneratorFactory;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.rschwietzke.jmh.FHS42b.FHS42b_FastHashSet;
import org.rschwietzke.jmh.FHS42b.Line;

@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 1, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(1)
public class FastHashSet_Benchmark
{
    static final String[] cities = {"Abha", "Abidjan", "Abéché", "Accra", "Addis Ababa", "Adelaide", "Aden", "Ahvaz", "Albuquerque", "Alexandra", "Alexandria", "Algiers", "Alice Springs", "Almaty", "Amsterdam", "Anadyr", "Anchorage", "Andorra la Vella", "Ankara", "Antananarivo", "Antsiranana", "Arkhangelsk", "Ashgabat", "Asmara", "Assab", "Astana", "Athens", "Atlanta", "Auckland", "Austin", "Baghdad", "Baguio", "Baku", "Baltimore", "Bamako", "Bangkok", "Bangui", "Banjul", "Barcelona", "Bata", "Batumi", "Beijing", "Beirut", "Belgrade", "Belize City", "Benghazi", "Bergen", "Berlin", "Bilbao", "Birao", "Bishkek", "Bissau", "Blantyre", "Bloemfontein", "Boise", "Bordeaux", "Bosaso", "Boston", "Bouaké", "Bratislava", "Brazzaville", "Bridgetown", "Brisbane", "Brussels", "Bucharest", "Budapest", "Bujumbura", "Bulawayo", "Burnie", "Busan", "Cabo San Lucas", "Cairns", "Cairo", "Calgary", "Canberra", "Cape Town", "Changsha", "Charlotte", "Chiang Mai", "Chicago", "Chihuahua", "Chișinău", "Chittagong", "Chongqing", "Christchurch", "City of San Marino", "Colombo", "Columbus", "Conakry", "Copenhagen", "Cotonou", "Cracow", "Da Lat", "Da Nang", "Dakar", "Dallas", "Damascus", "Dampier", "Dar es Salaam", "Darwin", "Denpasar", "Denver", "Detroit", "Dhaka", "Dikson", "Dili", "Djibouti", "Dodoma", "Dolisie", "Douala", "Dubai", "Dublin", "Dunedin", "Durban", "Dushanbe", "Edinburgh", "Edmonton", "El Paso", "Entebbe", "Erbil", "Erzurum", "Fairbanks", "Fianarantsoa", "Flores,  Petén", "Frankfurt", "Fresno", "Fukuoka", "Gabès", "Gaborone", "Gagnoa", "Gangtok", "Garissa", "Garoua", "George Town", "Ghanzi", "Gjoa Haven", "Guadalajara", "Guangzhou", "Guatemala City", "Halifax", "Hamburg", "Hamilton", "Hanga Roa", "Hanoi", "Harare", "Harbin", "Hargeisa", "Hat Yai", "Havana", "Helsinki", "Heraklion", "Hiroshima", "Ho Chi Minh City", "Hobart", "Hong Kong", "Honiara", "Honolulu", "Houston", "Ifrane", "Indianapolis", "Iqaluit", "Irkutsk", "Istanbul", "İzmir", "Jacksonville", "Jakarta", "Jayapura", "Jerusalem", "Johannesburg", "Jos", "Juba", "Kabul", "Kampala", "Kandi", "Kankan", "Kano", "Kansas City", "Karachi", "Karonga", "Kathmandu", "Khartoum", "Kingston", "Kinshasa", "Kolkata", "Kuala Lumpur", "Kumasi", "Kunming", "Kuopio", "Kuwait City", "Kyiv", "Kyoto", "La Ceiba", "La Paz", "Lagos", "Lahore", "Lake Havasu City", "Lake Tekapo", "Las Palmas de Gran Canaria", "Las Vegas", "Launceston", "Lhasa", "Libreville", "Lisbon", "Livingstone", "Ljubljana", "Lodwar", "Lomé", "London", "Los Angeles", "Louisville", "Luanda", "Lubumbashi", "Lusaka", "Luxembourg City", "Lviv", "Lyon", "Madrid", "Mahajanga", "Makassar", "Makurdi", "Malabo", "Malé", "Managua", "Manama", "Mandalay", "Mango", "Manila", "Maputo", "Marrakesh", "Marseille", "Maun", "Medan", "Mek'ele", "Melbourne", "Memphis", "Mexicali", "Mexico City", "Miami", "Milan", "Milwaukee", "Minneapolis", "Minsk", "Mogadishu", "Mombasa", "Monaco", "Moncton", "Monterrey", "Montreal", "Moscow", "Mumbai", "Murmansk", "Muscat", "Mzuzu", "N'Djamena", "Naha", "Nairobi", "Nakhon Ratchasima", "Napier", "Napoli", "Nashville", "Nassau", "Ndola", "New Delhi", "New Orleans", "New York City", "Ngaoundéré", "Niamey", "Nicosia", "Niigata", "Nouadhibou", "Nouakchott", "Novosibirsk", "Nuuk", "Odesa", "Odienné", "Oklahoma City", "Omaha", "Oranjestad", "Oslo", "Ottawa", "Ouagadougou", "Ouahigouya", "Ouarzazate", "Oulu", "Palembang", "Palermo", "Palm Springs", "Palmerston North", "Panama City", "Parakou", "Paris", "Perth", "Petropavlovsk-Kamchatsky", "Philadelphia", "Phnom Penh", "Phoenix", "Pittsburgh", "Podgorica", "Pointe-Noire", "Pontianak", "Port Moresby", "Port Sudan", "Port Vila", "Port-Gentil", "Portland (OR)", "Porto", "Prague", "Praia", "Pretoria", "Pyongyang", "Rabat", "Rangpur", "Reggane", "Reykjavík", "Riga", "Riyadh", "Rome", "Roseau", "Rostov-on-Don", "Sacramento", "Saint Petersburg", "Saint-Pierre", "Salt Lake City", "San Antonio", "San Diego", "San Francisco", "San Jose", "San José", "San Juan", "San Salvador", "Sana'a", "Santo Domingo", "Sapporo", "Sarajevo", "Saskatoon", "Seattle", "Ségou", "Seoul", "Seville", "Shanghai", "Singapore", "Skopje", "Sochi", "Sofia", "Sokoto", "Split", "St. John's", "St. Louis", "Stockholm", "Surabaya", "Suva", "Suwałki", "Sydney", "Tabora", "Tabriz", "Taipei", "Tallinn", "Tamale", "Tamanrasset", "Tampa", "Tashkent", "Tauranga", "Tbilisi", "Tegucigalpa", "Tehran", "Tel Aviv", "Thessaloniki", "Thiès", "Tijuana", "Timbuktu", "Tirana", "Toamasina", "Tokyo", "Toliara", "Toluca", "Toronto", "Tripoli", "Tromsø", "Tucson", "Tunis", "Ulaanbaatar", "Upington", "Ürümqi", "Vaduz", "Valencia", "Valletta", "Vancouver", "Veracruz", "Vienna", "Vientiane", "Villahermosa", "Vilnius", "Virginia Beach", "Vladivostok", "Warsaw", "Washington, D.C.", "Wau", "Wellington", "Whitehorse", "Wichita", "Willemstad", "Winnipeg", "Wrocław", "Xi'an", "Yakutsk", "Yangon", "Yaoundé", "Yellowknife", "Yerevan", "Yinchuan", "Zagreb", "Zanzibar City", "Zürich"};

    static class TestData
    {
        int pos = 0;
        int endToReload= 0;
        int newlinePos = -1;
        int end = 0;

        int lineStartPos = 0;
        int semicolonPos = -1;

        int hashCode;
        int temperature;
    }

    TestData[] data = new TestData[20000];
    Line line;

    @Setup
    public void setup()
    {
        var r = RandomGeneratorFactory.of("Xoroshiro128PlusPlus").create(4711L);

        line = new Line();
        int offset = 0;

        for (int i = 0; i < data.length; i++)
        {
            offset = line.add(cities[r.nextInt(cities.length)] + ";11.7", offset) + 1;

            var d = new TestData();
            d.end = line.end;
            d.endToReload = line.endToReload;
            d.newlinePos = line.newlinePos;
            d.pos = line.pos;
            d.lineStartPos = line.lineStartPos;
            d.semicolonPos = line.semicolonPos;
            d.hashCode = line.hashCode;
            d.temperature = line.temperature;

            data[i] = d;
        }
    }


    @Benchmark
    public FHS42b_FastHashSet measure()
    {
        var h = new FHS42b_FastHashSet(2000, 0.5f);
        var line = this.line;

        for (int i = 0; i < data.length; i++)
        {
            var d = data[i];
            line.end = d.end;
            line.endToReload = d.endToReload;
            line.newlinePos = d.newlinePos;
            line.pos = d.pos;
            line.lineStartPos = d.lineStartPos;
            line.semicolonPos = d.semicolonPos;
            line.hashCode = d.hashCode;
            line.temperature = d.temperature;

            h.putOrUpdate(line);
        }

        return h;
    }

    public static void main(String[] args) throws RunnerException
    {
        Options opt = new OptionsBuilder()
                .include(FastHashSet_Benchmark.class.getSimpleName())
                .forks(0)
                .build();

        new Runner(opt).run();
    }

}