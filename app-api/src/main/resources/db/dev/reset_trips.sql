-- Resets the local development trip catalog to the original seed data.
-- This script is intended for manual development use only.
-- Run from app-api with:
-- psql -U travlr_app -d travlr -f .\src\main\resources\db\dev\reset_trips.sql

TRUNCATE TABLE trips RESTART IDENTITY;

INSERT INTO trips (
	code,
	name,
	duration_days,
	start_date,
	resort,
	price_per_person,
	image_name,
	description
) VALUES
(
	'GALREE20270214',
	'Gale Reef',
	7,
	DATE '2027-02-14',
	'Emerald Bay, 3 stars',
	799.00,
	'reef1.jpg',
	'Enjoy a week of diving, relaxation, and ocean views at Gale Reef.'
),
(
	'DAWREE20270315',
	'Dawson''s Reef',
	5,
	DATE '2027-03-15',
	'Blue Lagoon, 4 stars',
	1199.00,
	'reef2.jpg',
	'Explore clear-water reef sites with guided excursions and resort amenities.'
),
(
	'CLAREE20270621',
	'Claire''s Reef',
	4,
	DATE '2027-06-21',
	'Coral Sands, 5 stars',
	1999.00,
	'reef3.jpg',
	'Experience a premium reef getaway with luxury lodging and coastal activities.'
),
(
	'MARISL20270910',
	'Mariner''s Isle',
	6,
	DATE '2027-09-10',
	'Azure Cove, 4 stars',
	1499.00,
	'kayak.jpg',
	'Spend six days kayaking, snorkeling, and relaxing along the sheltered coves of Mariner''s Isle.'
),
(
	'SUNCOA20271205',
	'Sunset Coast',
	3,
	DATE '2027-12-05',
	'Harbor Point, 3 stars',
	599.00,
	'sea-sound.jpg',
	'Enjoy a shorter coastal escape with beach access, local dining, and sunset views.'
),
(
	'TROPAL20280418',
	'Tropical Lagoon',
	10,
	DATE '2028-04-18',
	'Palm Vista, 5 stars',
	2499.00,
	'deluxe.jpg',
	'Experience a longer luxury lagoon retreat with guided excursions, premium lodging, and resort activities.'
);
